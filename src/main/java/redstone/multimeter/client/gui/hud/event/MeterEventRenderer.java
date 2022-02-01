package redstone.multimeter.client.gui.hud.event;

import net.minecraft.client.util.math.MatrixStack;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;

public abstract class MeterEventRenderer {
	
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
