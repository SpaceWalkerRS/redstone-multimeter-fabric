package rsmm.fabric.client.gui.log;

import static rsmm.fabric.client.gui.HudSettings.*;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;

public abstract class ToggleEventRenderer extends MeterEventRenderer {
	
	protected Mode mode;
	
	protected ToggleEventRenderer(MultimeterClient client, EventType type) {
		super(client, type);
	}
	
	@Override
	public void renderTickLogs(int x, int y, long firstTick, long lastTick, Meter meter) {
		updateMode(meter);
		
		y += GRID_SIZE;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		long lastHudTick = firstTick + COLUMN_COUNT;
		
		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}
		
		if (nextEvent == null) {
			if (isToggled(meter)) {
				draw(x + GRID_SIZE, y, color, (int)(lastHudTick - firstTick));
			}
			
			return;
		}
		
		long currentTick = -1;
		
		while (event == null || event.isBefore(lastHudTick)) {
			boolean eventInTable = (event != null && !event.isBefore(firstTick));
			boolean nextEventInTable = (nextEvent != null && nextEvent.isBefore(lastHudTick));
			
			if (eventInTable && event.getTick() != currentTick) {
				currentTick = event.getTick();
				
				int column = (int)(event.getTick() - firstTick);
				int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				if (wasToggled(event)) {
					drawOn(columnX, y, color);
				} else {
					drawOff(columnX, y, color);
				}
			}
			
			long start = eventInTable ? event.getTick() + 1 : firstTick;
			long end = nextEventInTable ? nextEvent.getTick() : lastHudTick;
			
			if (event == null ? !wasToggled(nextEvent) : wasToggled(event)) {
				int column = (int)(start - firstTick);
				int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				draw(columnX, y, color, (int)(end - start));
			}
			
			do {
				event = nextEvent;
				nextEvent = logs.getLog(type, ++index);
			} while (nextEvent != null && nextEvent.getTick() == currentTick);
			
			if (event == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderPulseLengths(int x, int y, long firstTick, long lastTick, Meter meter) {
		if (mode != Mode.ALL) {
			return;
		}
		
		y += GRID_SIZE;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		if (nextEvent == null) {
			return;
		}
		
		long lastHudTick = firstTick + COLUMN_COUNT;
		
		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}
		
		long currentTick = -1;
		
		while (event == null || event.isBefore(lastHudTick)) {
			boolean eventInTable = (event != null && !event.isBefore(firstTick));
			boolean nextEventInTable = (nextEvent != null && nextEvent.isBefore(lastHudTick));
			
			long start = eventInTable ? event.getTick() + 1 : firstTick;
			long end = nextEventInTable ? nextEvent.getTick() : lastHudTick;
			
			if (event != null && nextEvent != null) {
				long pulseLength = nextEvent.getTick() - event.getTick();
				
				if (pulseLength > 5) {
					int startX = x + (int)(start - firstTick) * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
					int endX = x + (int)(end - firstTick) * (COLUMN_WIDTH + GRID_SIZE) - GRID_SIZE;
					
					String text = String.valueOf(pulseLength);
					
					int availableWidth = endX - startX;
					int requiredWidth = font.getStringWidth(text) + 1;
					
					if (requiredWidth < availableWidth) {
						boolean toggled = wasToggled(event);
						
						int bgColor = toggled ? color : BACKGROUND_COLOR_TRANSPARENT;
						int textColor = toggled ? POWERED_TEXT_COLOR : UNPOWERED_TEXT_COLOR;
						
						fill(startX, y, startX + requiredWidth, y + ROW_HEIGHT, bgColor);
						font.draw(text, startX + 1, y + 1, textColor);
					}
				}
			}
			
			do {
				event = nextEvent;
				nextEvent = logs.getLog(type, ++index);
			} while (nextEvent != null && nextEvent.getTick() == currentTick);
			
			if (event == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderSubTickLogs(int x, int y, long tick, int subTickCount, Meter meter) {
		updateMode(meter);
		
		y += GRID_SIZE;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		if (nextEvent == null) {
			if (isToggled(meter)) {
				draw(x + GRID_SIZE, y, color, subTickCount);
			}
			
			return;
		}
		
		while (event == null || event.isBefore(tick, subTickCount)) {
			boolean eventInTable = (event != null && event.isAt(tick));
			boolean nextEventInTable = (nextEvent != null && nextEvent.isAt(tick));
			
			if (eventInTable) {
				int column = event.getSubTick();
				int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				if (wasToggled(event)) {
					drawOn(columnX, y, color);
				} else {
					drawOff(columnX, y, color);
				}
			}
			
			int start = eventInTable ? event.getSubTick() + 1 : 0;
			int end = nextEventInTable ? nextEvent.getSubTick() : subTickCount;
			
			if (event == null ? !wasToggled(nextEvent) : wasToggled(event)) {
				int columnX = x + start * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				draw(columnX, y, color, end - start);
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
	
	private void draw(int x, int y, int color) {
		switch (mode) {
		case ALL:
			fill(x, y, x + COLUMN_WIDTH, y + ROW_HEIGHT, color);
			break;
		case TOP:
			fill(x, y, x + COLUMN_WIDTH, y + ROW_HEIGHT - (ROW_HEIGHT / 2), color);
			break;
		case BOTTOM:
			fill(x, y + ROW_HEIGHT / 2, x + COLUMN_WIDTH, y + ROW_HEIGHT, color);
			break;
		default:
			break;
		}
	}
	
	private void draw(int x, int y, int color, int count) {
		for (int i = 0; i < count; i++) {
			draw(x + i * (COLUMN_WIDTH + GRID_SIZE), y, color);
		}
	}
	
	private void drawOn(int x, int y, int color) {
		switch (mode) {
		case ALL:
			fill(x + 1, y + 1, x + COLUMN_WIDTH - 1, y + ROW_HEIGHT - 1, color);
			break;
		case TOP:
			fill(x + 1, y + 1, x + COLUMN_WIDTH - 1, y + ROW_HEIGHT / 2, color);
			break;
		case BOTTOM:
			fill(x + 1, y + ROW_HEIGHT - (ROW_HEIGHT / 2), x + COLUMN_WIDTH - 1, y + ROW_HEIGHT - 1, color);
			break;
		default:
			break;
		}
	}
	
	private void drawOff(int x, int y, int color) {
		draw(x, y, color);
		drawOn(x, y, BACKGROUND_COLOR);
	}
	
	protected enum Mode {
		ALL, TOP, BOTTOM;
	}
}
