package rsmm.fabric.client.gui;

import static rsmm.fabric.client.gui.HudSettings.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.log.MeterEventRendererDispatcher;
import rsmm.fabric.common.Meter;

public class MultimeterHudRenderer extends DrawableHelper {
	
	private final MultimeterClient client;
	private final TextRenderer font;
	private final MeterEventRendererDispatcher eventRenderers;
	
	private int height;
	private int namesWidth;
	private int ticksWidth;
	private int subTicksWidth;
	
	private boolean paused;
	/** The offset between the current server tick and the last tick to be displayed in the ticks table */
	private int offset;
	
	public MultimeterHudRenderer(MultimeterClient client) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		this.eventRenderers = new MeterEventRendererDispatcher();
	}
	
	/**
	 * The offset between the current server tick and the last tick to be displayed in the ticks table
	 */
	public int getOffset() {
		return offset;
	}
	
	public void reset() {
		paused = false;
		offset = 0;
	}
	
	public void tick() {
		if (paused) {
			offset--;
		}
	}
	
	public void pause() {
		paused = !paused;
		
		if (!paused) {
			offset = 0;
		}
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
	
	/**
	 * Render the HUD that displays a 60 tick history
	 * of metered events in the meter group this client
	 * is subscribed to.
	 */
	public void render() {
		ROW_COUNT = client.getMeterGroup().getMeterCount();
		
		if (ROW_COUNT <= 0) {
			return;
		}
		
		height = ROW_COUNT * (ROW_HEIGHT + GRID_SIZE) + GRID_SIZE;
		namesWidth = getNamesWidth();
		ticksWidth = COLUMN_COUNT * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
		
		renderNamesTable();
		renderTicksTable();
		if (paused) {
			renderSubticksTable();
		}
		
		font.draw(client.getMeterGroup().getName(), 1, height + 2, METER_GROUP_NAME_COLOR);
	}
	
	private int getNamesWidth() {
		int width = 0;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			int nameWidth = font.getStringWidth(meter.getName());
			
			if (nameWidth > width) {
				width = nameWidth;
			}
		}
		
		return width + NAMES_TICKS_SPACING;
	}
	
	private void renderNamesTable() {
		drawBackground(0, 0, namesWidth, height);
		
		int x = 1;
		int y = 2;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			font.draw(meter.getName(), x, y, METER_NAME_COLOR);
			
			y += ROW_HEIGHT + GRID_SIZE;
		}
	}
	
	private void renderTicksTable() {
		int x = namesWidth;
		int y = 0;
		
		long firstTick = client.getCurrentServerTick() - COLUMN_COUNT + offset;
		
		drawBackground(x, y, ticksWidth, height);
		drawGridLines(x, y, COLUMN_COUNT, ROW_COUNT);
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			eventRenderers.renderTickLogs(font, x, y, firstTick, meter);
			
			y += ROW_HEIGHT + GRID_SIZE;
		}
		
		if (paused) {
			drawSelectedTickIndicator();
		}
	}
	
	private void renderSubticksTable() {
		long selectedTick = client.getCurrentServerTick() - (COLUMN_COUNT - SELECTED_COLUMN) + offset;
		int subTickCount = client.getMeterGroup().getLogManager().getSubTickCount(selectedTick);
		
		if (subTickCount <= 0) {
			return;
		}
		
		subTicksWidth = subTickCount * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
		
		int x = namesWidth + ticksWidth + TICKS_SUBTICKS_GAP;
		int y = 0;
		
		drawBackground(x, y, subTicksWidth, height);
		drawGridLines(x, y, subTickCount, ROW_COUNT);
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			eventRenderers.renderSubTickLogs(font, x, y, selectedTick, subTickCount, meter);
			
			y += ROW_HEIGHT + GRID_SIZE;
		}
	}
	
	private void drawBackground(int x, int y, int width, int height) {
		fill(x, y, x + width, y + height, BACKGROUND_COLOR);
	}
	
	private void drawGridLines(int startX, int startY, int columnCount, int rowCount) {
		int width = columnCount * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
		int height = rowCount * (ROW_HEIGHT + GRID_SIZE) + GRID_SIZE;
				
		// Vertical lines
		for (int i = 0; i <= columnCount; i++) {
			int x = startX + i * (COLUMN_WIDTH + GRID_SIZE);
			int color = (i > 0 && i < columnCount && i % 5 == 0) ? INTERVAL_GRID_COLOR : MAIN_GRID_COLOR;
			
			fill(x, startY, x + GRID_SIZE, startY + height, color);
		}
		// Horizontal lines
		for (int i = 0; i <= rowCount; i++) {
			int y = startY + i * (ROW_HEIGHT + GRID_SIZE);
			
			fill(startX, y, startX + width, y + GRID_SIZE, MAIN_GRID_COLOR);
		}
	}
	
	private void drawSelectedTickIndicator() {
		int x = namesWidth + SELECTED_COLUMN * (COLUMN_WIDTH + GRID_SIZE);
		int y = 0;
		
		int left = x;
		int right = x + (COLUMN_WIDTH + GRID_SIZE);
		int top = y;
		int bottom = y + ROW_COUNT * (ROW_HEIGHT + GRID_SIZE);
		
		fill(left            , top            , left  + GRID_SIZE, bottom            , SELECTED_TICK_INDICATOR_COLOR); // left
		fill(left + GRID_SIZE, top            , right + GRID_SIZE, top    + GRID_SIZE, SELECTED_TICK_INDICATOR_COLOR); // top
		fill(right           , top + GRID_SIZE, right + GRID_SIZE, bottom + GRID_SIZE, SELECTED_TICK_INDICATOR_COLOR); // right
		fill(left            , bottom         , right            , bottom + GRID_SIZE, SELECTED_TICK_INDICATOR_COLOR); // bottom
	}
}
