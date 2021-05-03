package rsmm.fabric.common.listeners;

import java.util.HashSet;
import java.util.Set;

import rsmm.fabric.common.MeterGroup;

public class MeterGroupChangeDispatcher {
	
	private static final Set<MeterGroupListener> LISTENERS = new HashSet<>();
	
	public static void addListener(MeterGroupListener listener) {
		LISTENERS.add(listener);
	}
	
	public static void removeListener(MeterGroupListener listener) {
		LISTENERS.remove(listener);
	}
	
	public static void cleared(MeterGroup meterGroup) {
		LISTENERS.forEach(listener -> listener.cleared(meterGroup));
	}
	
	public static void meterAdded(MeterGroup meterGroup, int index) {
		LISTENERS.forEach(listener -> listener.meterAdded(meterGroup, index));
	}
	
	public static void meterRemoved(MeterGroup meterGroup, int index) {
		LISTENERS.forEach(listener -> listener.meterRemoved(meterGroup, index));
	}
}
