package rsmm.fabric.common.log;

import java.util.HashMap;
import java.util.Map;

import rsmm.fabric.common.log.entry.*;

public enum LogType {
	
	POWERED_CHANGED(0, PoweredChangedLog.class),
	ACTIVE_CHANGED(1, ActiveChangedLog.class),
	BLOCK_MOVED(2, BlockMovedLog.class);
	
	private static final LogType[] TYPES;
	private static final Map<Class<? extends LogEntry>, LogType> LOG_TO_TYPE;
	
	static {
		TYPES = new LogType[values().length];
		LOG_TO_TYPE = new HashMap<>();
		
		for (LogType type : values()) {
			TYPES[type.index] = type;
			LOG_TO_TYPE.put(type.clazz, type);
		}
	}
	
	private final int index;
	private final Class<? extends LogEntry> clazz;
	
	private LogType(int index, Class<? extends LogEntry> clazz) {
		this.index = index;
		this.clazz = clazz;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Class<? extends LogEntry> getClazz() {
		return clazz;
	}
	
	public static LogType fromIndex(int index) {
		if (index < 0 || index >= TYPES.length) {
			return null;
		}
		
		return TYPES[index];
	}
	
	public static LogType fromLogEntry(LogEntry task) {
		return LOG_TO_TYPE.get(task.getClass());
	}
}
