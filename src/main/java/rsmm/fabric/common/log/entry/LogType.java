package rsmm.fabric.common.log.entry;

import java.util.HashMap;
import java.util.Map;

public class LogType<T extends LogEntry<?>> {
	
	private static final Map<String, LogType<?>> ALL;
	
	public static final LogType<BooleanLogEntry> POWERED;
	public static final LogType<BooleanLogEntry> ACTIVE;
	public static final LogType<DirectionLogEntry> MOVED;
	
	static {
		
		ALL = new HashMap<>();
		
		POWERED = register(new LogType<>("powered", BooleanLogEntry.class));
		ACTIVE = register(new LogType<>("active", BooleanLogEntry.class));
		MOVED = register(new LogType<>("moved", DirectionLogEntry.class));
	}
	
	private static <T extends LogEntry<?>> LogType<T> register(LogType<T> logType) {
		ALL.put(logType.getName(), logType);
		return logType;
	}
	
	public static LogType<?> fromName(String name) {
		return ALL.get(name);
	}
	
	private final String name;
	private final Class<T> entryType;
	
	private LogType(String name, Class<T> entryType) {
		this.name = name;
		this.entryType = entryType;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<T> entry() {
		return entryType;
	}
}
