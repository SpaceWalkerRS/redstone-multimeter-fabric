package rsmm.fabric.client.gui.log;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public abstract class MeterEventRenderer extends DrawableHelper {
	
	protected final EventType type;
	
	protected MeterEventRenderer(EventType type) {
		this.type = type;
	}
	
	public EventType getType() {
		return type;
	}
	
	public abstract void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, Meter meter);
	
	public abstract void renderSubTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter);
	
}
