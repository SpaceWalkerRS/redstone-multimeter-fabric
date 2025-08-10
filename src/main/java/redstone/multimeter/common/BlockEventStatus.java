package redstone.multimeter.common;

import java.util.HashMap;
import java.util.Map;

public enum BlockEventStatus {

	UNKNOWN(-1, "unknown"),
	QUEUED(3, "queued"),
	TRIGGERED(2, "triggered"),
	SUCCESS(0, "success"),
	FAILURE(1, "failure");

	public static final BlockEventStatus[] ALL;
	private static final Map<String, BlockEventStatus> BY_KEY;

	static {
		BlockEventStatus[] statuses = values();

		ALL = new BlockEventStatus[statuses.length - 1];
		BY_KEY = new HashMap<>();

		for (int i = 1; i < statuses.length; i++) {
			BlockEventStatus status = statuses[i];

			ALL[status.id] = status;
			BY_KEY.put(status.key, status);
		}
	}

	private final int id;
	private final String key;

	private BlockEventStatus(int id, String key) {
		this.id = id;
		this.key = key;
	}

	public int id() {
		return this.id;
	}

	public String key() {
		return this.key;
	}

	public static BlockEventStatus byId(int id) {
		if (id < 0 || id >= ALL.length) {
			return UNKNOWN;
		}

		return ALL[id];
	}

	public static BlockEventStatus byKey(String key) {
		return BY_KEY.getOrDefault(key, UNKNOWN);
	}
}
