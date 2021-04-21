package rsmm.fabric.common.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.PacketByteBuf;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.util.ListUtils;

public class MeterLogs {
	
	private final Map<EventType, List<MeterEvent>> eventLogs;
	
	private long lastLoggedTick = -1;
	
	public MeterLogs() {
		this.eventLogs = new HashMap<>();
	}
	
	public void clear() {
		eventLogs.clear();
		lastLoggedTick = -1;
	}
	
	public void add(MeterEvent event) {
		EventType type = event.getType();
		List<MeterEvent> logs = eventLogs.get(type);
		
		if (logs ==  null) {
			logs = new ArrayList<>();
			eventLogs.put(type, logs);
		}
		
		logs.add(event);
		
		if (event.getTick() > lastLoggedTick) {
			lastLoggedTick = event.getTick();
		}
	}
	
	public void clearOldLogs(long cutoff) {
		for (List<MeterEvent> logs : eventLogs.values()) {
			while (!logs.isEmpty()) {
				MeterEvent event = logs.get(0);
				
				if (event.getTick() > cutoff) {
					break;
				}
				
				logs.remove(0);
			}
		}
	}
	
	public MeterEvent getLog(EventType type, int index) {
		if (index < 0) {
			return null;
		}
		
		List<MeterEvent> logs = eventLogs.get(type);
		
		if (logs == null || index >= logs.size()) {
			return null;
		}
		
		return logs.get(index);
	}
	
	public int getLastLogBefore(EventType type, long tick) {
		return getLastLogBefore(type, tick, 0);
	}
	
	public int getLastLogBefore(EventType type, long tick, int subTick) {
		List<MeterEvent> logs = eventLogs.get(type);
		
		if (logs == null || logs.isEmpty() || tick <= logs.get(0).getTick()) {
			return -1;
		}
		if (tick > lastLoggedTick) {
			return logs.size() - 1;
		}
		
		int index = ListUtils.binarySearch(logs, event -> event.isBefore(tick, subTick));
		MeterEvent event = logs.get(index);
		
		while (!event.isBefore(tick, subTick)) {
			if (index == 0) {
				return -1;
			}
			
			event = logs.get(--index);
		}
		
		return index;
		
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(eventLogs.size());
		
		for (List<MeterEvent> logs : eventLogs.values()) {
			buffer.writeInt(logs.size());
			
			for (MeterEvent event : logs) {
				event.encode(buffer);
			}
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		int typeCount = buffer.readInt();
		
		for (int i = 0; i < typeCount; i++) {
			int logCount = buffer.readInt();
			
			for (int j = 0; j < logCount; j++) {
				MeterEvent event = new MeterEvent();
				event.decode(buffer);
				
				add(event);
			}
		}
	}
}
