package rsmm.fabric.common.log;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.log.entry.MeterActiveLogEntry;
import rsmm.fabric.common.log.entry.MeterPoweredLogEntry;
import rsmm.fabric.util.PacketUtils;

public class MeterGroupLogs {
	
	private final Map<Long, List<AbstractLogEntry>> logs;
	
	private long currentTick;
	
	public MeterGroupLogs() {
		this.logs = new LinkedHashMap<>();
	}
	
	public void clear() {
		logs.clear();
	}
	
	public void tick() {
		currentTick++;
	}
	
	public void syncTime(long currentTick) {
		this.currentTick = currentTick;
	}
	
	private void addLogEntry(AbstractLogEntry logEntry) {
		List<AbstractLogEntry> logEntries = logs.get(currentTick);
		
		if (logEntries == null) {
			logEntries = new LinkedList<>();
			logs.put(currentTick, logEntries);
		}
		
		logEntries.add(logEntry);
	}
	
	public void meterPoweredChanged(int index, boolean powered) {
		addLogEntry(new MeterPoweredLogEntry(index, powered));
	}
	
	public void meterActiveChanged(int index, boolean active) {
		addLogEntry(new MeterActiveLogEntry(index, active));
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(logs.size());
		
		for (Entry<Long, List<AbstractLogEntry>> entry : logs.entrySet()) {
			long tick = entry.getKey();
			List<AbstractLogEntry> logs = entry.getValue();
			
			buffer.writeLong(tick);
			buffer.writeInt(logs.size());
			
			for (AbstractLogEntry logEntry : logs) {
				PacketUtils.writeLogEntry(buffer, logEntry);
			}
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		int size = buffer.readInt();
		
		for (int i = 0; i < size; i++) {
			long tick = buffer.readLong();
			int logsCount = buffer.readInt();
			
			logs.putIfAbsent(tick, new LinkedList<>());
			List<AbstractLogEntry> logEntries = logs.get(tick);
			
			for (int j = 0; j < logsCount; j++) {
				AbstractLogEntry logEntry = PacketUtils.readLogEntry(buffer);
				
				if (logEntry != null) {
					logEntries.add(logEntry);
				}
			}
		}
	}
	
	public void print() {
		System.out.println("LOGS");
		
		for (Entry<Long, List<AbstractLogEntry>> entry : logs.entrySet()) {
			long tick = entry.getKey();
			List<AbstractLogEntry> logs = entry.getValue();
			
			System.out.println("----------------------------------");
			System.out.println("tick " + tick);
			
			for (int i = 0; i < logs.size(); i++) {
				System.out.print(i);
				logs.get(i).print();
			}
		}
		
		System.out.println("----------------------------------");
		System.out.println("----------------------------------");
	}
}
