package rsmm.fabric.common.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NbtCompound;

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
	
	private List<MeterEvent> getLogs(EventType type) {
		List<MeterEvent> logs = eventLogs.get(type);
		
		if (logs ==  null) {
			logs = new ArrayList<>();
			eventLogs.put(type, logs);
		}
		
		return logs;
	}
	
	public void add(MeterEvent event) {
		EventType type = event.getType();
		List<MeterEvent> logs = getLogs(type);
		
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
		
		if (logs == null || logs.isEmpty() || !logs.get(0).isBefore(tick, subTick)) {
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
	
	public MeterEvent getLastLogBefore(long tick) {
		return getLastLogBefore(tick, 0);
	}
	
	public MeterEvent getLastLogBefore(long tick, int subTick) {
		MeterEvent event = null;
		
		for (EventType type : EventType.TYPES) {
			int index = getLastLogBefore(type, tick, subTick);
			MeterEvent log = getLog(type, index);
			
			if (event == null || (log != null && log.isAfter(event))) {
				event = log;
			}
		}
		
		return event;
	}
	
	public NbtCompound toTag() {
		NbtCompound data = new NbtCompound();
		
		for (EventType type : eventLogs.keySet()) {
			data.put(type.getName(), toNBT(type));
		}
		
		return data;
	}
	
	public NbtCompound toNBT(EventType type) {
		NbtCompound data = new NbtCompound();
		
		List<MeterEvent> logs = eventLogs.get(type);
		int logCount = logs.size();
		
		for (int index = 0; index < logCount; index++) {
			MeterEvent event = logs.get(index);
			
			String key = String.valueOf(index);
			NbtCompound eventData = event.toNBT();
			
			data.put(key, eventData);
		}
		
		return data;
	}
	
	public void updateFromNBT(NbtCompound data) {
		for (String key : data.getKeys()) {
			EventType type = EventType.fromName(key);
			
			if (type != null) {
				updateFromNBT(data.getCompound(key), type);
			}
		}
	}
	
	public void updateFromNBT(NbtCompound data, EventType type) {
		Set<String> keys = data.getKeys();
		int size = keys.size();
		NbtCompound[] nbts = new NbtCompound[size];
		
		// Since the order of tags is not preserved
		// we need to re-order them first
		for (String key : keys) {
			try {
				int index = Integer.valueOf(key);
				NbtCompound eventData = data.getCompound(key);
				
				nbts[index] = eventData;
			} catch (NumberFormatException e) {
				
			} catch (IndexOutOfBoundsException e) {
				
			}
		}
		
		for (NbtCompound nbt : nbts) {
			if (nbt != null) {
				MeterEvent event = MeterEvent.createFromNBT(nbt);
				add(event);
			}
		}
	}
}
