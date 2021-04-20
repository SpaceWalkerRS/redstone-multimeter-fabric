package rsmm.fabric.common.event;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
	
	POWERED(0, "powered"),
	ACTIVE(1, "active"),
	MOVED(2, "moved");
	
	public static final EventType[] TYPES;
	private static final Map<String, EventType> BY_NAME;
	
	static {
		TYPES = new EventType[values().length];
		BY_NAME = new HashMap<>();
		
		for (EventType type : values()) {
			TYPES[type.index] = type;
			BY_NAME.put(type.name, type);
		}
	}
	
	private final int index;
	private final String name;
	
	private EventType(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static EventType fromIndex(int index) {
		if (index >= 0 && index < TYPES.length) {
			return TYPES[index];
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public static EventType fromName(String name) {
		return BY_NAME.get(name);
	}
	
	public int flag() {
		return 1 << index;
	}
}
