package rsmm.fabric.client.gui;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.log.MeterLogs;
import rsmm.fabric.common.log.entry.BooleanLogEntry;
import rsmm.fabric.common.log.entry.LogEntry;
import rsmm.fabric.common.log.entry.LogType;

public class MultimeterHudRenderer extends DrawableHelper {
	
	private static final int TICKS_SUBTICKS_GAP = 3; // Space between the ticks table and the subticks table
	
	private static final int COLUMN_WIDTH = 3; // Width of a column in the ticks and subticks tables
	private final int ROW_HEIGHT; // Height of a row
	private static final int GRID_SIZE = 1; // Thickness of the grid lines
	
	private static final int COLUMN_COUNT = 60;
	private static final int SELECTED_COLUMN = 44;
	private int ROW_COUNT;
	
	private static final int BACKGROUND_COLOR = 0xFF202020;
	private static final int MAJOR_GRID_COLOR = 0xFF606060;
	private static final int MINOR_GRID_COLOR = 0xFF404040;
	private static final int SELECTED_TICK_COLOR = 0xFFFFFFFF;
	
	private static final int POWERED_TEXT_COLOR = 0xFF000000;
	private static final int UNPOWERED_TEXT_COLOR = 0xFF707070;
	private static final int METER_NAME_COLOR = 0xFFFFFFFF;
	private static final int METER_GROUP_NAME_COLOR = 0xFF000000;
	
	private final MultimeterClient client;
	private final TextRenderer font;
	
	private long currentServerTick = -1;
	
	private int namesX;
	private int namesY;
	private int namesWidth;
	private int namesHeight;
	private int ticksX;
	private int ticksY;
	private int ticksWidth;
	private int ticksHeight;
	private int subTicksX;
	private int subTicksY;
	private int subTicksWidth;
	private int subTicksHeight;
	
	private long lastTick = -1; // The last (most recent) tick to be displayed in the ticks table
	private boolean paused;
	
	public MultimeterHudRenderer(MultimeterClient client) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = minecraftClient.textRenderer;
		
		ROW_HEIGHT = this.font.fontHeight;
	}
	
	public void tick() {
		currentServerTick++;
		
		if (!paused) {
			lastTick++;
		}
	}
	
	public void syncTime(long serverTick) {
		currentServerTick = serverTick;
		lastTick = currentServerTick;
	}
	
	public void onDisconnect() {
		currentServerTick = -1;
		lastTick = -1;
	}
	
	public void pause() {
		paused = !paused;
		
		if (!paused) {
			lastTick = currentServerTick;
		}
	}
	
	public void stepForward(int amount) {
		if (paused) {
			lastTick += amount;
		}
	}
	
	public void stepBackward(int amount) {
		if (paused) {
			lastTick -= amount;
		}
	}
	
	public void render(MatrixStack matrices) {
		ROW_COUNT = client.getMeterGroup().getMeterCount();
		
		if (ROW_COUNT <= 0) {
			return;
		}
		
		renderNamesTable(matrices);
		renderTicksTable(matrices);
		if (paused) {
			renderSubticksTable(matrices);
		}
		
		String text = client.getMeterGroup().getName();
		
		if (paused) {
			text += " (Paused)";
		}
		
		font.draw(matrices, text, namesX + 1, namesY + namesHeight + 2, METER_GROUP_NAME_COLOR);
	}
	
	private void renderNamesTable(MatrixStack matrices) {
		drawBackground(matrices, namesX, namesY, namesX + namesWidth, namesY + namesHeight);
		
		int x = namesX + 1;
		int y = namesY + 2;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			font.draw(matrices, meter.getName(), x, y, METER_NAME_COLOR);
			
			y += ROW_HEIGHT + GRID_SIZE;
		}
	}
	
	private void renderTicksTable(MatrixStack matrices) {
		drawBackground(matrices, ticksX, ticksY, ticksX + ticksWidth, ticksY + ticksHeight);
		
		drawGridLines(matrices, ticksX, ticksY, COLUMN_COUNT, ROW_COUNT);
		if (paused) {
			drawSelectedTickIndicator(matrices);
		}
	}
	
	private void renderSubticksTable(MatrixStack matrices) {
		drawBackground(matrices, subTicksX, subTicksY, subTicksX + subTicksWidth, subTicksY + subTicksHeight);
		drawGridLines(matrices, subTicksX, subTicksY, subTicksX + subTicksWidth, subTicksY + subTicksHeight);
	}
	
	private void drawBackground(MatrixStack matrices, int x0, int y0, int x1, int y1) {
		fill(matrices, x0, y0, x1, y1, BACKGROUND_COLOR);
	}
	
	private void drawGridLines(MatrixStack matrices, int startX, int startY, int columnCount, int rowCount) {
		int width = columnCount * (COLUMN_WIDTH + GRID_SIZE);
		int height = rowCount * (ROW_HEIGHT + GRID_SIZE);
				
		// Horizontal lines
		for (int i = 0; i <= rowCount; i++) {
			int y = startY + i * (ROW_HEIGHT + GRID_SIZE);
			
			fill(matrices, startX, y, startX + width, y + 1, MINOR_GRID_COLOR);
		}
		// Vertical lines
		for (int i = 0; i<= columnCount; i++) {
			int x = startX + i * (COLUMN_WIDTH + GRID_SIZE);
			int color = (i % 5 == 0) ? MAJOR_GRID_COLOR : MINOR_GRID_COLOR;
			
			fill(matrices, x, startY, x + 1, startY + height, color);
		}
	}
	
	private void drawSelectedTickIndicator(MatrixStack matrices) {
		int x = ticksX + SELECTED_COLUMN * (COLUMN_WIDTH + GRID_SIZE);
		int y = ticksY;
		
		int left = x;
		int right = x + COLUMN_WIDTH + GRID_SIZE;
		int top = y;
		int bottom = y + ROW_COUNT * (ROW_HEIGHT + GRID_SIZE);
		
		fill(matrices, left            , top            , left + GRID_SIZE , bottom            , SELECTED_TICK_COLOR); // left
		fill(matrices, left + GRID_SIZE, top            , right + GRID_SIZE, top + GRID_SIZE   , SELECTED_TICK_COLOR); // top
		fill(matrices, right           , top + GRID_SIZE, right + GRID_SIZE, bottom + GRID_SIZE, SELECTED_TICK_COLOR); // right
		fill(matrices, left            , bottom         , right            , bottom + GRID_SIZE, SELECTED_TICK_COLOR); // bottom
	}
}
