package redstone.multimeter.client.gui.hud.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

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

	public void renderTickLogs(PoseStack poses, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(poses, meter, renderer -> renderer.renderTickLogs(poses, x, y, firstTick, lastTick, meter));
	}

	public void renderPulseLengths(PoseStack poses, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(poses, meter, renderer -> renderer.renderPulseLengths(poses, x, y, firstTick, lastTick, meter));
	}

	public void renderSubtickLogs(PoseStack poses, int x, int y, long tick, int subTickCount, Meter meter) {
		renderMeterEvents(poses, meter, renderer -> renderer.renderSubtickLogs(poses, x, y, tick, subTickCount, meter));
	}

	private void renderMeterEvents(PoseStack poses, Meter meter, Consumer<MeterEventRenderer> consumer) {
		poses.pushPose();

		for (int index = EventType.ALL.length - 1; index >= 0; index--) {
			EventType type = EventType.ALL[index];

			if (meter.isMetering(type)) {
				consumer.accept(getEventRenderer(type));
			}

			poses.translate(0, 0, -0.1);
		}

		poses.popPose();
	}
}
