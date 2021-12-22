package redstone.multimeter.client.gui.hud.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;

public class MeterEventRenderDispatcher {
	
	private final Map<EventType, MeterEventRenderer> eventRenderers;
	private final BasicEventRenderer basicEventRenderer;
	
	public MeterEventRenderDispatcher(MultimeterHud hud) {
		this.eventRenderers = new HashMap<>();
		this.basicEventRenderer = new BasicEventRenderer(hud);
		
		registerEventRenderer(new PoweredEventRenderer(hud));
		registerEventRenderer(new ActiveEventRenderer(hud));
		registerEventRenderer(new PowerChangeEventRenderer(hud));
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
	
	public void renderTickLogs(int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(meter, renderer -> renderer.renderTickLogs(x, y, firstTick, lastTick, meter));
	}
	
	public void renderPulseLengths(int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(meter, renderer -> renderer.renderPulseLengths(x, y, firstTick, lastTick, meter));
	}
	
	public void renderSubtickLogs(int x, int y, long tick, int subTickCount, Meter meter) {
		renderMeterEvents(meter, renderer -> renderer.renderSubtickLogs(x, y, tick, subTickCount, meter));
	}
	
	private void renderMeterEvents(Meter meter, Consumer<MeterEventRenderer> consumer) {
		GL11.glPushMatrix();
		
		for (int index = EventType.ALL.length - 1; index >= 0; index--) {
			EventType type = EventType.ALL[index];
			
			if (meter.isMetering(type)) {
				consumer.accept(getEventRenderer(type));
			}
			
			GL11.glTranslated(0, 0, -0.1);
		}
		
		GL11.glPopMatrix();
	}
}
