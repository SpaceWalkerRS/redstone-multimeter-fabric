package rsmm.fabric.common.log.entry;

import java.util.HashMap;
import java.util.Map;

import rsmm.fabric.common.log.AbstractLogEntry;

public enum LogType {
	
	METER_POWRED(0, MeterPoweredLogEntry.class),
	METER_ACTIVE(1, MeterActiveLogEntry.class);
	
	private static final LogType[] TYPES;
	private static final Map<Class<? extends AbstractLogEntry>, LogType> LOG_TO_TYPE;
	
	static {
		TYPES = new LogType[values().length];
		LOG_TO_TYPE = new HashMap<>();
		
		for (LogType type : values()) {
			TYPES[type.index] = type;
			LOG_TO_TYPE.put(type.clazz, type);
		}
	}
	
	private final int index;
	private final Class<? extends AbstractLogEntry> clazz;
	
	private LogType(int index, Class<? extends AbstractLogEntry> clazz) {
		this.index = index;
		this.clazz = clazz;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Class<? extends AbstractLogEntry> getClazz() {
		return clazz;
	}
	
	public static LogType fromIndex(int index) {
		if (index < 0 || index >= TYPES.length) {
			return null;
		}
		
		return TYPES[index];
	}
	
	public static LogType fromLogEntry(AbstractLogEntry task) {
		return LOG_TO_TYPE.get(task.getClass());
	}
}
