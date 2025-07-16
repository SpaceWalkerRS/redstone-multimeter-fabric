package redstone.multimeter.client.gui.hud.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import redstone.multimeter.client.gui.GuiRenderer;
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
		registerEventRenderer(new ScheduledEventRenderer(hud, EventType.SCHEDULED_TICK));
		registerEventRenderer(new ScheduledEventRenderer(hud, EventType.BLOCK_EVENT));
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

	public void renderTickLogs(GuiRenderer renderer, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(renderer, meter, eventRenderer -> eventRenderer.renderTickLogs(renderer, x, y, firstTick, lastTick, meter));
	}

	public void renderPulseLengths(GuiRenderer renderer, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(renderer, meter, eventRenderer -> eventRenderer.renderPulseLengths(renderer, x, y, firstTick, lastTick, meter));
	}

	public void renderSubtickLogs(GuiRenderer renderer, int x, int y, long tick, int subTickCount, Meter meter) {
		renderMeterEvents(renderer, meter, eventRenderer -> eventRenderer.renderSubtickLogs(renderer, x, y, tick, subTickCount, meter));
	}

	private void renderMeterEvents(GuiRenderer renderer, Meter meter, Consumer<MeterEventRenderer> consumer) {
		renderer.pushMatrix();

		for (int index = EventType.ALL.length - 1; index >= 0; index--) {
			EventType type = EventType.ALL[index];

			if (meter.isMetering(type)) {
				consumer.accept(getEventRenderer(type));
			}

			renderer.translate(0, 0, -0.1);
		}

		renderer.popMatrix();
	}
}
