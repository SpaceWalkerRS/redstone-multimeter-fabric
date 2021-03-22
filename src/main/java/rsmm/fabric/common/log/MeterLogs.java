package rsmm.fabric.common.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.log.entry.LogEntry;
import rsmm.fabric.util.PacketUtils;

public class MeterLogs {
	
	private final List<LogEntry> logs;
	// Mapping of ticks to the indices of the first logs from those ticks
	private final Map<Long, Integer> tickToIndex;
	
	private long lastLoggedTick;
	
	public MeterLogs() {
		this.logs = new ArrayList<>();
		this.tickToIndex = new HashMap<>();
		
		this.lastLoggedTick = -1;
	}
	
	public boolean isEmpty() {
		return lastLoggedTick < 0;
	}
	
	public void clear() {
		logs.clear();
		tickToIndex.clear();
		lastLoggedTick = -1;
	}
	
	public long getLastLoggedTick() {
		return lastLoggedTick;
	}
	
	public void push(LogEntry log) {
		long tick = log.getTick();
		
		if (tick > lastLoggedTick) {
			lastLoggedTick = tick;
			tickToIndex.put(lastLoggedTick, logs.size());
			System.out.println("push - " + lastLoggedTick);
		}
		
		logs.add(log);
	}
	
	public void removeOldLogs(long lastAllowedTick) {
		if (isEmpty()) {
			return;
		}
		
		LogEntry log = logs.get(0);
		long tick = log.getTick();
		
		while (tick < lastAllowedTick) {
			long nextTick;
			
			do {
				logs.remove(0);
				
				log = logs.get(0);
				nextTick = log.getTick();
			} while (nextTick == tick);
			
			tick = nextTick;
		}
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(logs.size());
		
		for (LogEntry log : logs) {
			PacketUtils.writeLogEntry(buffer, log);
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		int logCount = buffer.readInt();
		
		for (int i = 0; i < logCount; i++) {
			LogEntry log = PacketUtils.readLogEntry(buffer);
			
			if (log != null) {
				push(log);
			}
		}
	}
	
	public void print() {
		for (LogEntry log : logs) {
			log.print();
		}
	}
}
