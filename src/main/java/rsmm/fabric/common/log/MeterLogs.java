package rsmm.fabric.common.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.util.ListUtils;
import rsmm.fabric.util.PacketUtils;

public class MeterLogs {
	
	private final Map<EventType<? extends MeterEvent>, List<MeterEvent>> eventLogs;
	
	private long lastLoggedTick = -1;
	
	public MeterLogs() {
		this.eventLogs = new HashMap<>();
	}
	
	public void clear() {
		eventLogs.clear();
		lastLoggedTick = -1;
	}
	
	public void add(MeterEvent event) {
		EventType<? extends MeterEvent> type = event.getType();
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
				
				if (event.getTick() >= cutoff) {
					break;
				}
				
				logs.remove(0);
			}
		}
	}
	
	public <T extends MeterEvent> T getLog(EventType<T> type, int index) {
		if (index < 0) {
			return null;
		}
		
		List<MeterEvent> logs = eventLogs.get(type);
		
		if (logs == null || index >= logs.size()) {
			return null;
		}
		
		return type.event().cast(logs.get(index));
	}
	
	public <T extends MeterEvent> T getLastLogBefore(EventType<T> type, long tick) {
		return getLastLogBefore(type, tick, 0);
	}
	
	public <T extends MeterEvent> T getLastLogBefore(EventType<T> type, long tick, long subTick) {
		List<MeterEvent> logs = eventLogs.get(type);
		
		if (logs == null || logs.isEmpty()) {
			return null;
		}
		
		if (tick > lastLoggedTick) {
			MeterEvent event = logs.get(logs.size() - 1);
			return type.event().cast(event);
		}
		
		int index = ListUtils.binarySearch(logs, event -> event.isBefore(tick, subTick));
		MeterEvent event = logs.get(index);
		
		if (!event.isBefore(tick, subTick)) {
			event = logs.get(index - 1);
		}
		
		return type.event().cast(event);
		
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(eventLogs.size());
		
		for (Entry<EventType<? extends MeterEvent>, List<MeterEvent>> entry : eventLogs.entrySet()) {
			EventType<?> type = entry.getKey();
			List<MeterEvent> logs = entry.getValue();
			
			buffer.writeString(type.getName());
			buffer.writeInt(logs.size());
			
			for (MeterEvent event : logs) {
				event.encode(buffer);
			}
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		int typeCount = buffer.readInt();
		
		for (int i = 0; i < typeCount; i++) {
			EventType<?> type = EventType.fromName(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
			int logCount = buffer.readInt();
			
			for (int j = 0; j < logCount; j++) {
				try {
					MeterEvent event = type.event().newInstance();
					event.decode(buffer);
					
					add(event);
				} catch (Exception e) {
					
				}
			}
		}
	}
}
