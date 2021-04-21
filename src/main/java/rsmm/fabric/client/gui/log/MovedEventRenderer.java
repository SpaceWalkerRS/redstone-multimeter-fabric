package rsmm.fabric.client.gui.log;

import static rsmm.fabric.client.gui.HudSettings.*;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;

public class MovedEventRenderer extends MeterEventRenderer {
	
	public MovedEventRenderer() {
		super(EventType.MOVED);
	}
	
	@Override
	public void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, Meter meter) {
		y += GRID_SIZE;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		long lastTick = firstTick + COLUMN_COUNT;
		
		while (event.isBefore(lastTick)) {
			int column = (int)(event.getTick() - firstTick);
			int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
			
			drawEvent(matrices, columnX, y, color);
			
			if ((event = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderSubTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter) {
		y += GRID_SIZE;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		while (event.isBefore(tick + 1)) {
			int column = event.getSubTick();
			int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
			
			drawEvent(matrices, columnX, y, color);
			
			if ((event = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	private void drawEvent(MatrixStack matrices, int x, int y, int color) {
		int half = ROW_HEIGHT / 2;
		
		fill(matrices, x, y + half - 1, x + COLUMN_WIDTH, y + ROW_HEIGHT - half + 1, color);
		fill(matrices, x, y + half, x + COLUMN_WIDTH, y + ROW_HEIGHT - half, 0xFFFFFFFF);
	}
}
