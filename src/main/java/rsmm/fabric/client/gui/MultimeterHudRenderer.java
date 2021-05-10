package rsmm.fabric.client.gui;

import static rsmm.fabric.client.gui.HudSettings.*;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.log.MeterEventRendererDispatcher;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.listeners.HudChangeDispatcher;
import rsmm.fabric.common.log.MeterLogs;

public class MultimeterHudRenderer extends DrawableHelper {
	
	private final MultimeterClient client;
	private final TextRenderer font;
	private final MeterEventRendererDispatcher eventRenderers;
	
	private boolean paused;
	/** The offset between the last server tick and the first tick to be displayed in the ticks table */
	private int offset;
	
	private int hoveredRow;
	private int hoveredNameColumn;
	private int hoveredTickColumn;
	private int hoveredSubTickColumn;
	
	public MultimeterHudRenderer(MultimeterClient client) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.eventRenderers = new MeterEventRendererDispatcher();
		
		this.reset();
	}
	
	public void resetOffset() {
		offset = 1 - COLUMN_COUNT;
	}
	
	public void reset() {
		paused = false;
		resetOffset();
		resetHoveredElements();
	}
	
	public void tick() {
		if (paused) {
			offset--;
		}
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void pause() {
		paused = !paused;
		
		if (!paused) {
			resetOffset();
		}
		
		HudChangeDispatcher.paused();
	}
	
	public void stepForward(int amount) {
		if (paused) {
			offset -= amount;
		}
	}
	
	public void stepBackward(int amount) {
		if (paused) {
			offset += amount;
		}
	}
	
	public long getSelectedTick() {
		return client.getLastServerTick() + offset + SELECTED_COLUMN;
	}
	
	public int getTableHeight() {
		return ROW_COUNT * (ROW_HEIGHT + GRID_SIZE) + GRID_SIZE;
	}
	
	public int getTotalHeight() {
		return (ROW_COUNT + 1) * (ROW_HEIGHT + GRID_SIZE) + GRID_SIZE;
	}
	
	public int getHoveredRow() {
		return hoveredRow;
	}
	
	public int getHoveredNameColumn() {
		return hoveredNameColumn;
	}
	
	public int getHoveredTickColumn() {
		return hoveredTickColumn;
	}
	
	public int getHoveredSubTickColumn() {
		return hoveredSubTickColumn;
	}
	
	public void render() {
		render(0, 0);
	}
	
	/**
	 * Render the HUD that displays a 60 tick history
	 * of metered events in the meter group this client
	 * is subscribed to.
	 */
	public void render(int x, int y) {
		updateRowCount();
		
		if (ROW_COUNT <= 0) {
			return;
		}
		
		int namesWidth = getNamesWidth();
		int height = getTableHeight();
		
		renderNamesTable(x, y, namesWidth, height);
		renderTicksTable(x + namesWidth, y, TICKS_TABLE_WIDTH, height);
		if (paused) {
			renderSubTicksTable(x + namesWidth + TICKS_TABLE_WIDTH + TICKS_SUB_TICKS_GAP, y, height);
		}
		
		int color = client.hasMultimeterScreenOpen() ? METER_GROUP_NAME_COLOR_LIGHT : METER_GROUP_NAME_COLOR_DARK;
		font.draw(client.getMeterGroup().getName(), x + 2, y + height + 2, color);
	}
	
	private void updateRowCount() {
		ROW_COUNT = client.getMeterGroup().getMeterCount();
	}
	
	public int getNamesWidth() {
		int width = 0;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			int nameWidth = font.getStringWidth(meter.getName());
			
			if (nameWidth > width) {
				width = nameWidth;
			}
		}
		
		return width + NAMES_TICKS_SPACING;
	}
	
	private void renderNamesTable(int x, int y, int width, int height) {
		drawBackground(x, y, width, height);
		
		int nameX = x + 2;
		int nameY = y + 2;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			font.draw(meter.getName(), nameX, nameY, METER_NAME_COLOR);
			
			nameY += ROW_HEIGHT + GRID_SIZE;
		}
	}
	
	private void renderTicksTable(int x, int y, int width, int height) {
		long firstTick = getSelectedTick() - SELECTED_COLUMN;
		long currentTick = client.getLastServerTick() + 1;
		
		int markedColumn = (currentTick < firstTick || currentTick > (firstTick + COLUMN_COUNT)) ? -1 : (int)(currentTick - firstTick);
		
		drawBackground(x, y, width, height);
		drawGridLines(x, y, height, COLUMN_COUNT, markedColumn);
		
		int rowX = x;
		int rowY = y;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			eventRenderers.renderTickLogs(font, rowX, rowY, firstTick, currentTick, meter);
			
			rowY += ROW_HEIGHT + GRID_SIZE;
		}
		
		if (paused) {
			drawSelectedTickIndicator(x + SELECTED_COLUMN * (COLUMN_WIDTH + GRID_SIZE), y);
		}
	}
	
	private void renderSubTicksTable(int x, int y, int height) {
		long selectedTick = getSelectedTick();
		int subTickCount = client.getMeterGroup().getLogManager().getSubTickCount(selectedTick);
		
		if (subTickCount <= 0) {
			return;
		}
		
		int width = subTickCount * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
		
		drawBackground(x, y, width, height);
		drawGridLines(x, y, height, subTickCount);
		
		int rowX = x;
		int rowY = y;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			eventRenderers.renderSubTickLogs(font, rowX, rowY, selectedTick, subTickCount, meter);
			
			rowY += ROW_HEIGHT + GRID_SIZE;
		}
	}
	
	private void drawBackground(int x, int y, int width, int height) {
		fill(x, y, x + width, y + height, BACKGROUND_COLOR);
	}
	
	private void drawGridLines(int x, int y, int height, int columnCount) {
		drawGridLines(x, y, height, columnCount, -1);
	}
	
	private void drawGridLines(int x, int y, int height, int columnCount, int markedColumn) {
		int width = columnCount * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
		// Vertical lines
		for (int i = 0; i <= columnCount; i++) {
			int lineX = x + i * (COLUMN_WIDTH + GRID_SIZE);
			int color = (i > 0 && i < columnCount && i % 5 == 0) ? INTERVAL_GRID_COLOR : MAIN_GRID_COLOR;
			
			fill(lineX, y, lineX + GRID_SIZE, y + height, color);
		}
		// Horizontal lines
		for (int i = 0; i <= ROW_COUNT; i++) {
			int lineY = y + i * (ROW_HEIGHT + GRID_SIZE);
			
			fill(x, lineY, x + width, lineY + GRID_SIZE, MAIN_GRID_COLOR);
		}
		// Marked column
		if (markedColumn >= 0 && markedColumn <= columnCount) {
			int lineX = x + markedColumn * (COLUMN_WIDTH + GRID_SIZE);
			int color = MARKER_GRID_COLOR;
			
			fill(lineX, y + GRID_SIZE, lineX + GRID_SIZE, y + height - GRID_SIZE, color);
		}
	}
	
	private void drawSelectedTickIndicator(int x, int y) {
		drawSelectionIndicator(x, y, COLUMN_WIDTH + GRID_SIZE, ROW_COUNT * (ROW_HEIGHT + GRID_SIZE));
	}
	
	public void renderSelectedMeterIndicator(int x, int y, int selectedMeter) {
		if (selectedMeter >= 0) {
			int namesWidth = getNamesWidth();
			
			int indicatorX = x;
			int indicatorY = y + selectedMeter * (ROW_HEIGHT + GRID_SIZE);
			int indicatorHeight = ROW_HEIGHT + GRID_SIZE;
			int indicatorWidth = namesWidth - GRID_SIZE;
			
			drawSelectionIndicator(indicatorX, indicatorY, indicatorWidth, indicatorHeight);
		}
	}
	
	private void drawSelectionIndicator(int x, int y, int width, int height) {
		int left = x;
		int right = x + width;
		int top = y;
		int bottom = y + height;
		
		fill(left            , top            , left  + GRID_SIZE, bottom            , SELECTION_INDICATOR_COLOR); // left
		fill(left + GRID_SIZE, top            , right + GRID_SIZE, top    + GRID_SIZE, SELECTION_INDICATOR_COLOR); // top
		fill(right           , top + GRID_SIZE, right + GRID_SIZE, bottom + GRID_SIZE, SELECTION_INDICATOR_COLOR); // right
		fill(left            , bottom         , right            , bottom + GRID_SIZE, SELECTION_INDICATOR_COLOR); // bottom
	}
	
	private void resetHoveredElements() {
		hoveredRow = -1;
		hoveredNameColumn = -1;
		hoveredTickColumn = -1;
		hoveredSubTickColumn = -1;
	}
	
	public void updateHoveredElements(int x, int y, double mouseX, double mouseY) {
		updateRowCount();
		resetHoveredElements();
		
		if (ROW_COUNT >= 0) {
			int height = getTableHeight();
			
			if (mouseY >= y && mouseY <= (y + height)) {
				hoveredRow = (int)((mouseY - y) / (ROW_HEIGHT + GRID_SIZE));
				
				if (hoveredRow >= ROW_COUNT) {
					hoveredRow = ROW_COUNT - 1;
				}
				
				int width = getNamesWidth();
				
				if (mouseX >= x && mouseX <= (x + width)) {
					hoveredNameColumn = 0;
					return;
				}
				
				if (!paused) {
					return;
				}
				
				x += width;
				width = TICKS_TABLE_WIDTH;
				
				if (mouseX >= x && mouseX <= (x + width)) {
					hoveredTickColumn = (int)((mouseX - x) / (COLUMN_WIDTH + GRID_SIZE));
					
					if (hoveredTickColumn >= COLUMN_COUNT) {
						hoveredTickColumn = COLUMN_COUNT - 1;
					}
					
					return;
				}
				
				long selectedTick = getSelectedTick();
				int subTickCount = client.getMeterGroup().getLogManager().getSubTickCount(selectedTick);
				
				x += width + TICKS_SUB_TICKS_GAP;
				width = subTickCount * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
				
				if (mouseX >= x && mouseX <= (x + width)) {
					hoveredSubTickColumn = (int)((mouseX - x) / (COLUMN_WIDTH + GRID_SIZE));
					
					if (hoveredSubTickColumn >= subTickCount) {
						hoveredSubTickColumn = subTickCount - 1;
					}
				}
			}
		}
	}
	
	public List<List<Text>> getTextForTooltip() {
		if (hoveredRow >= 0) { 
			if (hoveredSubTickColumn >= 0) {
				long tick = getSelectedTick();
				int subTick = hoveredSubTickColumn;
				
				Meter meter = client.getMeterGroup().getMeter(hoveredRow);
				MeterLogs logs = meter.getLogs();
				MeterEvent event = logs.getLastLogBefore(tick, subTick + 1);
				
				if (event != null && event.isAt(tick, subTick)) {
					return event.getTextForTooltip();
				}
			}
		}
		
		return Collections.emptyList();
	}
}
