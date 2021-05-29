package rsmm.fabric.common.listeners;

import java.util.LinkedHashSet;
import java.util.Set;

public class HudChangeDispatcher {
	
	private static final Set<HudListener> LISTENERS = new LinkedHashSet<>();
	
	public static void addListener(HudListener listener) {
		LISTENERS.add(listener);
	}
	
	public static void removeListener(HudListener listener) {
		LISTENERS.remove(listener);
	}
	
	public static void paused() {
		LISTENERS.forEach(listener -> listener.paused());
	}
}
