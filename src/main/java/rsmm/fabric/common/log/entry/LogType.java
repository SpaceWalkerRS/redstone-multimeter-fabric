package rsmm.fabric.common.log.entry;

import java.util.HashMap;
import java.util.Map;

public class LogType {
	
	private static final Map<String, LogType> ALL;
	
	public static final LogType POWERED;
	public static final LogType ACTIVE;
	public static final LogType MOVED;
	
	static {
		
		ALL = new HashMap<>();
		
		POWERED = register(new LogType("powered", BooleanLogEntry.class));
		ACTIVE = register(new LogType("active", BooleanLogEntry.class));
		MOVED = register(new LogType("moved", DirectionLogEntry.class));
	}
	
	private static LogType register(LogType logType) {
		ALL.put(logType.getName(), logType);
		return logType;
	}
	
	public static LogType fromName(String name) {
		return ALL.get(name);
	}
	
	private final String name;
	private final Class<?> entryType;
	
	private LogType(String name, Class<?> entryType) {
		this.name = name;
		this.entryType = entryType;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> entry() {
		return entryType;
	}
}
