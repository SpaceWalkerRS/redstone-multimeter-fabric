package rsmm.fabric.common.task;

import java.util.HashMap;
import java.util.Map;

public enum TaskType {
	
	TOGGLE_METER(0, ToggleMeterTask.class),
	ADD_METER(1, AddMeterTask.class),
	REMOVE_METER(2, RemoveMeterTask.class),
	REMOVE_METERS(3, RemoveMetersTask.class),
	RENAME_METER(4, RenameMeterTask.class),
	RECOLOR_METER(5, RecolorMeterTask.class);
	
	private static final TaskType[] TYPES;
	private static final Map<Class<? extends MultimeterTask>, TaskType> TASK_TO_TYPE;
	
	static {
		TYPES = new TaskType[values().length];
		TASK_TO_TYPE = new HashMap<>();
		
		for (TaskType type : values()) {
			TYPES[type.index] = type;
			TASK_TO_TYPE.put(type.clazz, type);
		}
	}
	
	private final int index;
	private final Class<? extends MultimeterTask> clazz;
	
	private TaskType(int index, Class<? extends MultimeterTask> clazz) {
		this.index = index;
		this.clazz = clazz;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Class<? extends MultimeterTask> getClazz() {
		return clazz;
	}
	
	public static TaskType fromIndex(int index) {
		if (index < 0 || index >= TYPES.length) {
			return null;
		}
		
		return TYPES[index];
	}
	
	public static TaskType fromTask(MultimeterTask task) {
		return TASK_TO_TYPE.get(task.getClass());
	}
}
