package rsmm.fabric.client.gui.hud.event;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.gui.hud.HudRenderer;
import rsmm.fabric.client.gui.hud.MultimeterHud;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public abstract class MeterEventRenderer implements HudRenderer {
	
	protected final MultimeterHud hud;
	
	protected EventType type;
	
	protected MeterEventRenderer(MultimeterHud hud, EventType type) {
		this.hud = hud;
		
		this.type = type;
	}
	
	public EventType getType() {
		return type;
	}
	
	public abstract void renderTickLogs(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter);
	
	public void renderPulseLengths(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter) {
		
	}
	
	public abstract void renderSubtickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter);
	
}
