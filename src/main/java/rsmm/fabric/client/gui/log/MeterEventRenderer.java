package rsmm.fabric.client.gui.log;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public abstract class MeterEventRenderer extends DrawableHelper {
	
	protected final MultimeterClient client;
	protected final TextRenderer font;
	
	protected EventType type;
	
	protected MeterEventRenderer(MultimeterClient client, EventType type) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		
		this.type = type;
	}
	
	public EventType getType() {
		return type;
	}
	
	public abstract void renderTickLogs(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter);
	
	public void renderPulseLengths(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter) {
		
	}
	
	public abstract void renderSubTickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter);
	
}
