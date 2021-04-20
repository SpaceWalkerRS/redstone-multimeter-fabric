package rsmm.fabric.client.gui.log;

import static rsmm.fabric.client.gui.HudSettings.*;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;

public abstract class ToggleEventRenderer extends MeterEventRenderer {
	
	protected Mode mode;
	
	protected ToggleEventRenderer(EventType type) {
		super(type);
	}
	
	@Override
	public void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, Meter meter) {
		updateMode(meter);
		
		y += GRID_SIZE;
		int color = meter.getColor();
		boolean drawPulseLength = (mode == Mode.ALL);
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		if (nextEvent == null) {
			if (isToggled(meter)) {
				draw(matrices, x + GRID_SIZE, y, color, COLUMN_COUNT);
			}
			
			return;
		}
		
		long lastTick = firstTick + COLUMN_COUNT;
		
		while (event == null || event.isBefore(lastTick)) {
			boolean eventInTable = (event != null && !event.isBefore(firstTick));
			boolean nextEventInTable = (nextEvent != null && nextEvent.isBefore(lastTick));
			
			if (eventInTable) {
				int column = (int)(event.getTick() - firstTick);
				int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				if (wasToggled(event)) {
					drawOn(matrices, columnX, y, color);
				} else {
					drawOff(matrices, columnX, y, color);
				}
			}
			
			long start = eventInTable ? event.getTick() + 1 : firstTick;
			long end = nextEventInTable ? nextEvent.getTick() : lastTick;
			
			if (nextEvent == null ? isToggled(meter) : !wasToggled(nextEvent)) {
				int column = (int)(start - firstTick);
				int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				draw(matrices, columnX, y, color, (int)(end - start));
			}
			
			if (drawPulseLength && event != null && nextEvent != null) {
				long pulseLength = nextEvent.getTick() - event.getTick();
				
				if (pulseLength > 5) {
					int startX = x + (int)(start - firstTick) * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
					int endX = x + (int)(end - firstTick) * (COLUMN_WIDTH + GRID_SIZE) - GRID_SIZE;
					
					String text = String.valueOf(pulseLength);
					
					int availableWidth = endX - startX;
					int requiredWidth = font.getWidth(text);
					
					if (requiredWidth < availableWidth) {
						boolean toggled = wasToggled(event);
						
						int bgColor = toggled ? color : BACKGROUND_COLOR;
						int textColor = toggled ? POWERED_TEXT_COLOR : UNPOWERED_TEXT_COLOR;
						
						fill(matrices, startX, y, startX + requiredWidth, y + ROW_HEIGHT, bgColor);
						font.draw(matrices, text, startX + 1, y + 1, textColor);
					}
				}
			}
			
			do {
				event = nextEvent;
				nextEvent = logs.getLog(type, ++index);
			} while (event != null && nextEvent != null && event.getTick() == nextEvent.getTick());
			
			if (event == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderSubTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter) {
		updateMode(meter);
		
		y += GRID_SIZE;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		if (nextEvent == null) {
			if (isToggled(meter)) {
				draw(matrices, x + GRID_SIZE, y, color, subTickCount);
			}
			
			return;
		}
		
		while (event == null || !event.isAfter(tick)) {
			boolean eventInTable = (event != null && event.isAt(tick));
			boolean nextEventInTable = (nextEvent != null && nextEvent.isAt(tick));
			
			if (eventInTable) {
				int column = event.getSubTick();
				int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				if (wasToggled(event)) {
					drawOn(matrices, columnX, y, color);
				} else {
					drawOff(matrices, columnX, y, color);
				}
			}
			
			int start = eventInTable ? event.getSubTick() + 1 : 0;
			int end = nextEventInTable ? nextEvent.getSubTick() : subTickCount;
			
			if (nextEvent == null ? isToggled(meter) : !wasToggled(nextEvent)) {
				int columnX = x + start * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				draw(matrices, columnX, y, color, end - start);
			}
			
			event = nextEvent;
			nextEvent = logs.getLog(type, ++index);
			
			if (event == null) {
				break;
			}
		}
	}
	
	protected abstract void updateMode(Meter meter);
	
	private boolean wasToggled(MeterEvent event) {
		return (event.getMetaData() & 1) != 0;
	}
	
	protected abstract boolean isToggled(Meter meter);
	
	private void draw(MatrixStack matrices, int x, int y, int color) {
		switch (mode) {
		case ALL:
			fill(matrices, x, y, x + COLUMN_WIDTH, y + ROW_HEIGHT, color);
			break;
		case TOP:
			fill(matrices, x, y, x + COLUMN_WIDTH, y + ROW_HEIGHT - (ROW_HEIGHT / 2), color);
			break;
		case BOTTOM:
			fill(matrices, x, y + ROW_HEIGHT / 2, x + COLUMN_WIDTH, y + ROW_HEIGHT, color);
			break;
		default:
			break;
		}
	}
	
	private void draw(MatrixStack matrices, int x, int y, int color, int count) {
		for (int i = 0; i < count; i++) {
			draw(matrices, x + i * (COLUMN_WIDTH + GRID_SIZE), y, color);
		}
	}
	
	private void drawOn(MatrixStack matrices, int x, int y, int color) {
		switch (mode) {
		case ALL:
			fill(matrices, x + 1, y + 1, x + COLUMN_WIDTH - 1, y + ROW_HEIGHT - 1, color);
			break;
		case TOP:
			fill(matrices, x + 1, y + 1, x + COLUMN_WIDTH - 1, y + ROW_HEIGHT / 2, color);
			break;
		case BOTTOM:
			fill(matrices, x + 1, y + ROW_HEIGHT - (ROW_HEIGHT / 2), x + COLUMN_WIDTH - 1, y + ROW_HEIGHT - 1, color);
			break;
		default:
			break;
		}
	}
	
	private void drawOff(MatrixStack matrices, int x, int y, int color) {
		draw(matrices, x, y, color);
		drawOn(matrices, x, y, BACKGROUND_COLOR);
	}
	
	protected enum Mode {
		ALL, TOP, BOTTOM;
	}
}
