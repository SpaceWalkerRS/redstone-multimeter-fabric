package rsmm.fabric.client.gui.log;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public class MeterEventRendererDispatcher {
	
	private final MultimeterClient client;
	private final Map<EventType, MeterEventRenderer> eventRenderers;
	private final BasicEventRenderer basicEventRenderer;
	
	public MeterEventRendererDispatcher(MultimeterClient client) {
		this.client = client;
		this.eventRenderers = new HashMap<>();
		this.basicEventRenderer = new BasicEventRenderer(this.client);
		
		registerEventRenderer(new PoweredEventRenderer(this.client));
		registerEventRenderer(new ActiveEventRenderer(this.client));
		registerEventRenderer(new PowerChangeEventRenderer(this.client));
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
	
	public void renderTickLogs(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter) {
		for (EventType type : EventType.TYPES) {
			if (meter.isMetering(type)) {
				getEventRenderer(type).renderTickLogs(matrices, x, y, firstTick, lastTick, meter);
			}
		}
		for (EventType type : EventType.TYPES) {
			if (meter.isMetering(type)) {
				getEventRenderer(type).renderPulseLengths(matrices, x, y, firstTick, lastTick, meter);
			}
		}
	}
	
	public void renderSubTickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter) {
		for (EventType type : EventType.TYPES) {
			if (meter.isMetering(type)) {
				getEventRenderer(type).renderSubTickLogs(matrices, x, y, tick, subTickCount, meter);
			}
		}
	}
}
