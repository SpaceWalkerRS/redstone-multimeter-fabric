package rsmm.fabric.common.log;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

import rsmm.fabric.RedstoneMultimeterMod;
import rsmm.fabric.common.log.entry.BooleanLogEntry;
import rsmm.fabric.common.log.entry.DirectionLogEntry;
import rsmm.fabric.common.log.entry.LogEntry;
import rsmm.fabric.common.log.entry.LogType;
import rsmm.fabric.util.ListUtils;
import rsmm.fabric.util.PacketUtils;

public class MeterLogs {
	
	private final List<LogEntry<?>> logs;
	
	private long lastLoggedTick = -1;
	
	public MeterLogs() {
		this.logs = new ArrayList<>();
	}
	
	public void push(LogEntry<?> log) {
		long tick = log.getTick();
		
		if (tick > lastLoggedTick) {
			lastLoggedTick = tick;
		}
		
		logs.add(log);
	}
	
	public void clear() {
		logs.clear();
		
		lastLoggedTick = -1;
	}
	
	public void clearOldLogs(long cutoff) {
		while (!logs.isEmpty()) {
			LogEntry<?> log = logs.get(0);
			
			if (log.getTick() >= cutoff) {
				break;
			}
			
			logs.remove(0);
		}
	}
	
	public LogEntry<?> getLog(int index) {
		if (index >= 0 && index < logs.size()) {
			return logs.get(index);
		}
		
		return null;
	}
	
	public LogEntry<?> getLastLogBefore(long tick) {
		return getLastLogBefore(tick, 0);
	}
	
	public LogEntry<?> getLastLogBefore(long tick, long subTick) {
		int logIndex = getLastLogIndexBefore(tick, subTick);
		
		if (logIndex < 0) {
			return null;
		}
		
		return logs.get(logIndex);
	}
	
	public <T> LogEntry<T> getLastLogBefore(long tick, LogType<? extends LogEntry<T>> type) {
		return getLastLogBefore(tick, 0, type);
	}
	
	public <T> LogEntry<T> getLastLogBefore(long tick, long subTick, LogType<? extends LogEntry<T>> type) {
		int logIndex = getLastLogIndexBefore(tick, subTick);
		
		if (logIndex < 0) {
			return null;
		}
		
		LogEntry<?> log = logs.get(logIndex);
		
		while (log.getType() != type) {
			if (--logIndex < 0) {
				return null;
			}
			
			log = logs.get(logIndex);
		}
		
		return type.entry().cast(log);
	}
	
	private int getLastLogIndexBefore(long tick, long subTick) {
		if (tick > lastLoggedTick) {
			return logs.size() - 1;
		}
		
		int logIndex = ListUtils.binarySearch(logs, log -> log.isBefore(tick, subTick));
		
		LogEntry<?> log = logs.get(logIndex);
		
		if (!log.isBefore(tick, subTick)) {
			logIndex--;
		}
		
		return logIndex;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(logs.size());
		
		for (LogEntry<?> log : logs) {
			PacketUtils.writeLogEntry(buffer, log);
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		int logCount = buffer.readInt();
		
		for (int i = 0; i < logCount; i++) {
			LogEntry<?> log = PacketUtils.readLogEntry(buffer);
			
			if (log != null) {
				push(log);
				
				RedstoneMultimeterMod.LOGGER.info(log.getTick() + " " + log.getSubTick() + " - " + log.getType().getName() + ": " + log.get());
			}
		}
	}
	
	private void test() {
		push(new BooleanLogEntry(LogType.POWERED, 0, 0, true));
		push(new BooleanLogEntry(LogType.ACTIVE, 0, 1, true));
		push(new BooleanLogEntry(LogType.POWERED, 1, 2, false));
		push(new BooleanLogEntry(LogType.ACTIVE, 1, 3, false));
		push(new DirectionLogEntry(LogType.MOVED, 2, 4, Direction.NORTH));
		push(new BooleanLogEntry(LogType.POWERED, 4, 0, true));
		push(new BooleanLogEntry(LogType.POWERED, 5, 3, false));
		push(new DirectionLogEntry(LogType.MOVED, 5, 4, Direction.SOUTH));
		
		print(getLastLogBefore(0));
		print(getLastLogBefore(0, 1));
		print(getLastLogBefore(1));
		print(getLastLogBefore(1, 3));
		print(getLastLogBefore(1, LogType.ACTIVE));
		print(getLastLogBefore(1, 4, LogType.ACTIVE));
		print(getLastLogBefore(3, 2));
		print(getLastLogBefore(4, LogType.POWERED));
		print(getLastLogBefore(5, 3, LogType.POWERED));
		print(getLastLogBefore(5, 4, LogType.POWERED));
	}
	
	private void print(LogEntry<?> log) {
		if (log == null) {
			System.out.println("NO LOG");
			return;
		}
		
		System.out.println(log.getTick() + " " + log.getSubTick() + ": " + log.getType().getName() + " - " + log.get());
	}
	
	public static void TEST() {
		new MeterLogs().test();
	}
}
