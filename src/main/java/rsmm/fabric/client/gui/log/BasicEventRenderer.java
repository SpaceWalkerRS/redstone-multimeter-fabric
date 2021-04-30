package rsmm.fabric.client.gui.log;

import static rsmm.fabric.client.gui.HudSettings.COLUMN_COUNT;
import static rsmm.fabric.client.gui.HudSettings.COLUMN_WIDTH;
import static rsmm.fabric.client.gui.HudSettings.GRID_SIZE;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;

public abstract class BasicEventRenderer extends MeterEventRenderer {
	
	public BasicEventRenderer(EventType type) {
		super(type);
	}
	
	@Override
	public void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, Meter meter) {
		y += GRID_SIZE;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		long lastTick = firstTick + COLUMN_COUNT;
		
		while (event.isBefore(lastTick)) {
			int column = (int)(event.getTick() - firstTick); // The event is no older than 1M ticks, so we can safely cast to int
			int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
			
			drawEvent(matrices, columnX, y, meter, event);
			
			if ((event = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderSubTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter) {
		y += GRID_SIZE;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		while (event.isBefore(tick, subTickCount)) {
			int column = event.getSubTick();
			int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
			
			drawEvent(matrices, columnX, y, meter, event);
			
			if ((event = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	protected abstract void drawEvent(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event);
	
}