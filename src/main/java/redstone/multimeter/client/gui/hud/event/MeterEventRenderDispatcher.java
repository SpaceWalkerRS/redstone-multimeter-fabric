package redstone.multimeter.client.gui.hud.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.util.math.MatrixStack;
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
	
	public void renderTickLogs(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(matrices, meter, renderer -> renderer.renderTickLogs(matrices, x, y, firstTick, lastTick, meter));
	}
	
	public void renderPulseLengths(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(matrices, meter, renderer -> renderer.renderPulseLengths(matrices, x, y, firstTick, lastTick, meter));
	}
	
	public void renderSubtickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter) {
		renderMeterEvents(matrices, meter, renderer -> renderer.renderSubtickLogs(matrices, x, y, tick, subTickCount, meter));
	}
	
	private void renderMeterEvents(MatrixStack matrices, Meter meter, Consumer<MeterEventRenderer> consumer) {
		matrices.push();
		
		for (int index = EventType.ALL.length - 1; index >= 0; index--) {
			EventType type = EventType.ALL[index];
			
			if (meter.isMetering(type)) {
				consumer.accept(getEventRenderer(type));
			}
			
			matrices.translate(0, 0, -0.1);
		}
		
		matrices.pop();
	}
}
