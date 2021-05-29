package rsmm.fabric.common.listeners;

import java.util.LinkedHashSet;
import java.util.Set;

import rsmm.fabric.common.Meter;

public class MeterChangeDispatcher {
	
	private static final Set<MeterListener> LISTENERS = new LinkedHashSet<>();
	
	public static void addListener(MeterListener listener) {
		LISTENERS.add(listener);
	}
	
	public static void removeListener(MeterListener listener) {
		LISTENERS.remove(listener);
	}
	
	public static void posChanged(Meter meter) {
		LISTENERS.forEach(listener -> listener.posChanged(meter));
	}
	
	public static void nameChanged(Meter meter) {
		LISTENERS.forEach(listener -> listener.nameChanged(meter));
	}
	
	public static void colorChanged(Meter meter) {
		LISTENERS.forEach(listener -> listener.colorChanged(meter));
	}
	
	public static void isMovableChanged(Meter meter) {
		LISTENERS.forEach(listener -> listener.isMovableChanged(meter));
	}
	
	public static void meteredEventsChanged(Meter meter) {
		LISTENERS.forEach(listener -> listener.meteredEventsChanged(meter));
	}
	
	public static void isHiddenChanged(Meter meter) {
		LISTENERS.forEach(listener -> listener.isHiddenChanged(meter));
	}
}
