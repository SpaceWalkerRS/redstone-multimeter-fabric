package rsmm.fabric.common.event;

import java.util.HashMap;
import java.util.Map;

import rsmm.fabric.RedstoneMultimeterMod;

public class EventType<T extends MeterEvent> {
	
	private static final Map<String,EventType<? extends MeterEvent>> ALL;
	
	public static final EventType<PoweredEvent> POWERED;
	public static final EventType<ActiveEvent> ACTIVE;
	public static final EventType<MovedEvent> MOVED;
	
	static {
		
		ALL = new HashMap<>();
		
		POWERED = register(new EventType<>("powered", PoweredEvent.class));
		ACTIVE = register(new EventType<>("active", ActiveEvent.class));
		MOVED = register(new EventType<>("moved", MovedEvent.class));
	}
	
	private static <T extends MeterEvent> EventType<T> register(EventType<T> type) {
		if (ALL.putIfAbsent(type.getName(), type) == null) {
			return type;
		} else {
			RedstoneMultimeterMod.LOGGER.warn(String.format("Cannot register EventType %s, as a type with that name already exists!", type.getName()));
			
			return null;
		}
	}
	
	public static EventType<?> fromName(String name) {
		return ALL.get(name);
	}
	
	private final String name;
	private final Class<T> event;
	
	private EventType(String name, Class<T> event) {
		this.name = name;
		this.event = event;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<T> event() {
		return event;
	}
}
