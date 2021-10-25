package rsmm.fabric.client.gui.hud.event;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import rsmm.fabric.client.gui.hud.MultimeterHud;
import rsmm.fabric.client.option.Options;
import rsmm.fabric.common.meter.Meter;
import rsmm.fabric.common.meter.event.EventType;
import rsmm.fabric.common.meter.event.MeterEvent;
import rsmm.fabric.common.meter.log.MeterLogs;

public abstract class ToggleEventRenderer extends MeterEventRenderer {
	
	protected Mode mode;
	
	protected ToggleEventRenderer(MultimeterHud client, EventType type) {
		super(client, type);
	}
	
	@Override
	public void renderTickLogs(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter) {
		updateMode(meter);
		
		y += hud.settings.gridSize;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		long lastHudTick = firstTick + Options.HUD.COLUMN_COUNT.get();
		
		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}
		
		if (nextEvent == null) {
			if (isToggled(meter)) {
				draw(matrices, x + hud.settings.gridSize, y, color, (int)(lastHudTick - firstTick));
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
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
				
				if (wasToggled(event)) {
					drawOn(matrices, columnX, y, color);
				} else {
					drawOff(matrices, columnX, y, color);
				}
			}
			
			long start = eventInTable ? event.getTick() + 1 : firstTick;
			long end = nextEventInTable ? nextEvent.getTick() : lastHudTick;
			
			if (event == null ? !wasToggled(nextEvent) : wasToggled(event)) {
				int column = (int)(start - firstTick);
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
				
				draw(matrices, columnX, y, color, (int)(end - start));
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
	public void renderPulseLengths(MatrixStack matrices, int x, int y, long firstTick, long lastTick, Meter meter) {
		updateMode(meter);
		
		if (mode != Mode.ALL) {
			return;
		}
		
		y += hud.settings.gridSize;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		if (nextEvent == null) {
			return;
		}
		
		long lastHudTick = firstTick + Options.HUD.COLUMN_COUNT.get();
		
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
					int startX = x + (int)(start - firstTick) * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
					int endX = x + (int)(end - firstTick) * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
					
					Text text = new LiteralText(String.valueOf(pulseLength));
					
					int availableWidth = endX - startX;
					int requiredWidth = hud.font.getWidth(text) + 1;
					
					if (requiredWidth < availableWidth) {
						boolean toggled = wasToggled(event);
						
						int bgColor = toggled ? color : hud.settings.colorBackground;
						int textColor = toggled ? hud.settings.colorTextOn : hud.settings.colorTextOff;
						
						matrices.push();
						hud.renderer.drawText(matrices, text, startX + 1, y + 1, textColor);
						matrices.translate(0, 0, -0.01);
						hud.renderer.drawRect(matrices, startX, y, requiredWidth, hud.settings.rowHeight, bgColor);
						matrices.pop();
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
	public void renderSubtickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter) {
		updateMode(meter);
		
		y += hud.settings.gridSize;
		int color = meter.getColor();
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick);
		MeterEvent event = logs.getLog(type, index);
		MeterEvent nextEvent = logs.getLog(type, ++index);
		
		if (nextEvent == null) {
			if (isToggled(meter)) {
				draw(matrices, x + hud.settings.gridSize, y, color, subTickCount);
			}
			
			return;
		}
		
		while (event == null || event.isBefore(tick, subTickCount)) {
			boolean eventInTable = (event != null && event.isAt(tick));
			boolean nextEventInTable = (nextEvent != null && nextEvent.isAt(tick));
			
			if (eventInTable) {
				int column = event.getSubTick();
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
				
				if (wasToggled(event)) {
					drawOn(matrices, columnX, y, color);
				} else {
					drawOff(matrices, columnX, y, color);
				}
			}
			
			int start = eventInTable ? event.getSubTick() + 1 : 0;
			int end = nextEventInTable ? nextEvent.getSubTick() : subTickCount;
			
			if (event == null ? !wasToggled(nextEvent) : wasToggled(event)) {
				int columnX = x + start * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
				
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
		int width = hud.settings.columnWidth;
		int height = hud.settings.rowHeight;
		
		if (mode != Mode.ALL) {
			height = height - (height / 2); // round up
			
			if (mode == Mode.BOTTOM) {
				y += (hud.settings.rowHeight - height);
			}
		}
		
		hud.renderer.drawRect(matrices, x, y, width, height, color);
	}
	
	private void draw(MatrixStack matrices, int x, int y, int color, int count) {
		for (int i = 0; i < count; i++) {
			draw(matrices, x + i * (hud.settings.columnWidth + hud.settings.gridSize), y, color);
		}
	}
	
	private void drawOn(MatrixStack matrices, int x, int y, int color) {
		x += 1;
		y += 1;
		int width = hud.settings.columnWidth - 2;
		int height = hud.settings.rowHeight - 2;
		
		if (mode != Mode.ALL) {
			height /= 2;
			
			if (mode == Mode.BOTTOM) {
				y += (hud.settings.rowHeight - (height + 2));
			}
		}
		
		hud.renderer.drawRect(matrices, x, y, width, height, color);
	}
	
	private void drawOff(MatrixStack matrices, int x, int y, int color) {
		matrices.push();
		drawOn(matrices, x, y, hud.settings.colorBackground);
		matrices.translate(0, 0, -0.01);
		draw(matrices, x, y, color);
		matrices.pop();
	}
	
	protected enum Mode {
		ALL, TOP, BOTTOM;
	}
}
