package redstone.multimeter.common.meter.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.util.ListUtils;
import redstone.multimeter.util.NbtUtils;

public class MeterLogs {

	private final List<EventLog>[] eventLogs;

	private long count = 0;

	public MeterLogs() {
		@SuppressWarnings("unchecked")
		List<EventLog>[] lists = new List[EventType.ALL.length];

		for (int index = 0; index < lists.length; index++) {
			lists[index] = new ArrayList<>();
		}

		this.eventLogs = lists;
	}

	public void clear() {
		for (List<EventLog> logs : eventLogs) {
			logs.clear();
		}

		count = 0;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	private List<EventLog> getLogs(EventType type) {
		return eventLogs[type.getIndex()];
	}

	public void add(EventLog log) {
		EventType type = log.getEvent().getType();
		List<EventLog> logs = getLogs(type);

		logs.add(getLastLogBefore(type, log.getTick(), log.getSubtick()) + 1, log);

		count++;
	}

	public void clearOldLogs(long cutoff) {
		for (List<EventLog> logs : eventLogs) {
			while (!logs.isEmpty()) {
				EventLog log = logs.get(0);

				if (log.getTick() > cutoff) {
					break;
				}

				logs.remove(0);
			}
		}
	}

	public EventLog getLog(EventType type, int index) {
		if (index < 0) {
			return null;
		}

		List<EventLog> logs = getLogs(type);

		if (index >= logs.size()) {
			return null;
		}

		return logs.get(index);
	}

	public int getLastLogBefore(EventType type, long tick) {
		return getLastLogBefore(type, tick, 0);
	}

	public int getLastLogBefore(EventType type, long tick, int subtick) {
		List<EventLog> logs = getLogs(type);

		if (logs.isEmpty() || !logs.get(0).isBefore(tick, subtick)) {
			return -1;
		}
		if (logs.get(logs.size() - 1).isBefore(tick, subtick)) {
			return logs.size() - 1;
		}

		int index = ListUtils.binarySearch(logs, event -> event.isBefore(tick, subtick));
		EventLog log = logs.get(index);

		while (!log.isBefore(tick, subtick)) {
			if (index == 0) {
				return -1;
			}

			log = logs.get(--index);
		}

		return index;
	}

	public EventLog getLastLogBefore(long tick) {
		return getLastLogBefore(tick, 0);
	}

	public EventLog getLastLogBefore(long tick, int subtick) {
		EventLog lastLog = null;

		for (EventType type : EventType.ALL) {
			int index = getLastLogBefore(type, tick, subtick);
			EventLog log = getLog(type, index);

			if (lastLog == null || (log != null && log.isAfter(lastLog))) {
				lastLog = log;
			}
		}

		return lastLog;
	}

	public EventLog getLogAt(long tick, int subtick) {
		EventLog log = getLastLogBefore(tick, subtick + 1);
		return log != null && log.isAt(tick, subtick) ? log : null;
	}

	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();

		for (EventType type : EventType.ALL) {
			ListTag logs = toNbt(type);

			if (!logs.isEmpty()) {
				nbt.put(type.getName(), logs);
			}
		}

		return nbt;
	}

	private ListTag toNbt(EventType type) {
		ListTag list = new ListTag();

		for (EventLog log : getLogs(type)) {
			list.add(log.toNbt());
		}

		return list;
	}

	public static Collection<EventLog> fromNbt(CompoundTag nbt) {
		Collection<EventLog> logs = new ArrayList<>();

		for (String key : nbt.getAllKeys()) {
			EventType type = EventType.byName(key);

			if (type != null) {
				logs.addAll(fromNbt(type, nbt.getList(key, NbtUtils.TYPE_COMPOUND)));
			}
		}

		return logs;
	}

	public static Collection<EventLog> fromNbt(EventType type, ListTag nbt) {
		Collection<EventLog> logs = new ArrayList<>();

		for (int i = 0; i < nbt.size(); i++) {
			logs.add(EventLog.fromNbt(nbt.getCompound(i)));
		}

		return logs;
	}
}
