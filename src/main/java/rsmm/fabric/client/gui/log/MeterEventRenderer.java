package rsmm.fabric.client.gui.log;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;

public abstract class MeterEventRenderer<T extends MeterEvent> {
	
	private static final Map<EventType<? extends MeterEvent>, MeterEventRenderer<? extends MeterEvent>> ALL;
	
	public static <T extends MeterEvent> void register(EventType<T> type, MeterEventRenderer<T> renderer) {
		ALL.put(type, renderer);
	}
	
	public static void render(MatrixStack matrices, EventType<? extends MeterEvent> type, Meter meter, int x, int y) {
		MeterEventRenderer<? extends MeterEvent> renderer = ALL.get(type);
		
		if (renderer != null) {
			renderer.render(matrices, meter, x, y);
		}
	}
	
	static {
		
		ALL = new HashMap<>();
	}
	
	public void render(MatrixStack matrices, Meter meter, int x, int y) {
		
	}
}
