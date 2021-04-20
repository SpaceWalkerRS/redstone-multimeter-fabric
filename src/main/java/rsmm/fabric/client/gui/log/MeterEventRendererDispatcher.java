package rsmm.fabric.client.gui.log;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public class MeterEventRendererDispatcher {
	
	private final List<MeterEventRenderer> eventRenderers;
	
	public MeterEventRendererDispatcher() {
		eventRenderers = new LinkedList<>();
		
		eventRenderers.add(new PoweredEventRenderer());
		eventRenderers.add(new ActiveEventRenderer());
		eventRenderers.add(new MovedEventRenderer());
	}
	
	public void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, Meter meter) {
		for (MeterEventRenderer eventRenderer : eventRenderers) {
			EventType type = eventRenderer.getType();
			
			if (meter.isMetering(type)) {
				eventRenderer.renderTickLogs(matrices, font, x, y, firstTick, meter);
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
