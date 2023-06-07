package redstone.multimeter.client.gui.hud.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;

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

	public void renderTickLogs(GuiGraphics graphics, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(graphics, meter, renderer -> renderer.renderTickLogs(graphics, x, y, firstTick, lastTick, meter));
	}

	public void renderPulseLengths(GuiGraphics graphics, int x, int y, long firstTick, long lastTick, Meter meter) {
		renderMeterEvents(graphics, meter, renderer -> renderer.renderPulseLengths(graphics, x, y, firstTick, lastTick, meter));
	}

	public void renderSubtickLogs(GuiGraphics graphics, int x, int y, long tick, int subTickCount, Meter meter) {
		renderMeterEvents(graphics, meter, renderer -> renderer.renderSubtickLogs(graphics, x, y, tick, subTickCount, meter));
	}

	private void renderMeterEvents(GuiGraphics graphics, Meter meter, Consumer<MeterEventRenderer> consumer) {
		PoseStack poses = graphics.pose();

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
