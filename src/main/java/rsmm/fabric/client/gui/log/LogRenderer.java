package rsmm.fabric.client.gui.log;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.RedstoneMultimeterMod;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public abstract class LogRenderer {
	
	private static final Map<EventType, LogRenderer> ALL = new HashMap<>();
	
	public static void register(LogRenderer renderer) {
		ALL.put(renderer.getType(), renderer);
	}
	
	public static  void render(MatrixStack matrices, EventType type, Meter meter, int x, int y) {
		LogRenderer renderer = ALL.get(type);
		
		if (renderer == null) {
			RedstoneMultimeterMod.LOGGER.warn(String.format("LogType %s has not registered a LogRenderer!", type));
		} else {
			renderer.render(matrices, meter, x, y);
		}
	}
	
	private final EventType type;
	
	protected LogRenderer(EventType type) {
		this.type = type;
	}
	
	public EventType getType() {
		return type;
	}
	
	public abstract void render(MatrixStack matrices, Meter meter, int x, int y);
	
}
