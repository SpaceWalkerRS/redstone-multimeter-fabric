package rsmm.fabric.client.gui.log;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public class MeterEventRendererDispatcher {
	
	private final Map<EventType, MeterEventRenderer> eventRenderers;
	private final BasicEventRenderer basicEventRenderer;
	
	public MeterEventRendererDispatcher() {
		eventRenderers = new HashMap<>();
		basicEventRenderer = new BasicEventRenderer();
		
		registerEventRenderer(new PoweredEventRenderer());
		registerEventRenderer(new ActiveEventRenderer());
		registerEventRenderer(new PowerChangeEventRenderer());
	}
	
	private void registerEventRenderer(MeterEventRenderer eventRenderer) {
		eventRenderers.put(eventRenderer.getType(), eventRenderer);
	}
	
	private MeterEventRenderer getEventRenderer(EventType type) {
		MeterEventRenderer eventRenderer = eventRenderers.get(type);
		
		if (eventRenderer == null) {
			basicEventRenderer.setType(type);
			return basicEventRenderer;
		}
		
		return eventRenderer;
	}
	
	public void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, long lastTick, Meter meter) {
		for (EventType type : EventType.TYPES) {
			if (meter.isMetering(type)) {
				getEventRenderer(type).renderTickLogs(matrices, font, x, y, firstTick, lastTick, meter);
			}
		}
	}
	
	public void renderSubTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter) {
		for (EventType type : EventType.TYPES) {
			if (meter.isMetering(type)) {
				getEventRenderer(type).renderSubTickLogs(matrices, font, x, y, tick, subTickCount, meter);
			}
		}
	}
}
