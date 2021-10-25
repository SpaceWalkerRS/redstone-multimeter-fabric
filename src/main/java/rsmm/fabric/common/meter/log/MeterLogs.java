package rsmm.fabric.common.meter.log;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import rsmm.fabric.common.meter.event.EventType;
import rsmm.fabric.common.meter.event.MeterEvent;
import rsmm.fabric.util.ListUtils;

public class MeterLogs {
	
	private final List<MeterEvent>[] eventLogs;
	
	private long lastLoggedTick = -1;
	
	public MeterLogs() {
		@SuppressWarnings("unchecked")
		List<MeterEvent>[] lists = new List[EventType.ALL.length];
		
		for (int index = 0; index < lists.length; index++) {
			lists[index] = new ArrayList<>();
		}
		
		this.eventLogs = lists;
	}
	
	public void clear() {
		for (List<MeterEvent> logs : eventLogs) {
			logs.clear();
		}
		
		lastLoggedTick = -1;
	}
	
	public boolean isEmpty() {
		return lastLoggedTick < 0;
	}
	
	private List<MeterEvent> getLogs(EventType type) {
		return eventLogs[type.getIndex()];
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
		for (List<MeterEvent> logs : eventLogs) {
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
		
		List<MeterEvent> logs = getLogs(type);
		
		if (index >= logs.size()) {
			return null;
		}
		
		return logs.get(index);
	}
	
	public int getLastLogBefore(EventType type, long tick) {
		return getLastLogBefore(type, tick, 0);
	}
	
	public int getLastLogBefore(EventType type, long tick, int subick) {
		List<MeterEvent> logs = getLogs(type);
		
		if (logs.isEmpty() || !logs.get(0).isBefore(tick, subick)) {
			return -1;
		}
		if (tick > lastLoggedTick) {
			return logs.size() - 1;
		}
		
		int index = ListUtils.binarySearch(logs, event -> event.isBefore(tick, subick));
		MeterEvent event = logs.get(index);
		
		while (!event.isBefore(tick, subick)) {
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
	
	public MeterEvent getLastLogBefore(long tick, int subtick) {
		MeterEvent event = null;
		
		for (EventType type : EventType.ALL) {
			int index = getLastLogBefore(type, tick, subtick);
			MeterEvent log = getLog(type, index);
			
			if (event == null || (log != null && log.isAfter(event))) {
				event = log;
			}
		}
		
		return event;
	}
	
	public MeterEvent getLogAt(long tick, int subtick) {
		MeterEvent event = getLastLogBefore(tick, subtick + 1);
		return event != null && event.isAt(tick, subtick) ? event : null;
	}
	
	public NbtCompound toNBT() {
		NbtCompound nbt = new NbtCompound();
		
		for (EventType type : EventType.ALL) {
			NbtList logs = toNBT(type);
			
			if (!logs.isEmpty()) {
				nbt.put(type.getName(), logs);
			}
		}
		
		return nbt;
	}
	
	private NbtList toNBT(EventType type) {
		NbtList list = new NbtList();
		
		for (MeterEvent event : getLogs(type)) {
			list.add(event.toNBT());
		}
		
		return list;
	}
	
	public void updateFromNBT(NbtCompound nbt) {
		for (String key : nbt.getKeys()) {
			EventType type = EventType.fromName(key);
			
			if (type != null) {
				updateFromNBT(type, nbt.getList(key, 10));
			}
		}
	}
	
	public void updateFromNBT(EventType type, NbtList logs) {
		for (int index = 0; index < logs.size(); index++) {
			NbtCompound nbt = logs.getCompound(index);
			MeterEvent event = MeterEvent.fromNBT(nbt);
			
			add(event);
		}
	}
}
