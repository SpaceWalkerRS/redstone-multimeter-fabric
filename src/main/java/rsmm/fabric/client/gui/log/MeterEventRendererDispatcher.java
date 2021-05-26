package rsmm.fabric.client.gui.log;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public class MeterEventRendererDispatcher {
	
	private final List<MeterEventRenderer> eventRenderers;
	
	public MeterEventRendererDispatcher() {
		eventRenderers = new ArrayList<>();
		
		eventRenderers.add(new PoweredEventRenderer());
		eventRenderers.add(new ActiveEventRenderer());
		eventRenderers.add(new BasicEventRenderer(EventType.MOVED));
		eventRenderers.add(new PowerChangeEventRenderer());
		eventRenderers.add(new BasicEventRenderer(EventType.RANDOM_TICK));
		eventRenderers.add(new BasicEventRenderer(EventType.SCHEDULED_TICK));
		eventRenderers.add(new BasicEventRenderer(EventType.BLOCK_EVENT));
		eventRenderers.add(new BasicEventRenderer(EventType.ENTITY_TICK));
		eventRenderers.add(new BasicEventRenderer(EventType.BLOCK_ENTITY_TICK));
	}
	
	public void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, long lastTick, Meter meter) {
		for (MeterEventRenderer eventRenderer : eventRenderers) {
			EventType type = eventRenderer.getType();
			
			if (meter.isMetering(type)) {
				eventRenderer.renderTickLogs(matrices, font, x, y, firstTick, lastTick, meter);
			}
		}
	}
	
	public void renderSubTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter) {
		for (MeterEventRenderer eventRenderer : eventRenderers) {
			EventType type = eventRenderer.getType();
			
			if (meter.isMetering(type)) {
				eventRenderer.renderSubTickLogs(matrices, font, x, y, tick, subTickCount, meter);
			}
		}
	}
}
