package rsmm.fabric.client;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
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
	
	private static final int COLUMNS_COUNT = 60;
	private static final int SELECTED_COLUMN = 44;
	
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
	
	private int rowCount;
	
	private long selectedTick = -1;
	private boolean paused;
	
	public MultimeterHudRenderer(MultimeterClient client) {
		MinecraftClient mcClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = mcClient.textRenderer;
		
		ROW_HEIGHT = font.fontHeight;
	}
	
	public void tick() {
		currentServerTick++;
		
		if (!paused) {
			selectedTick++;
		}
	}
	
	public void syncTime(long serverTick) {
		currentServerTick = serverTick;
		selectedTick = currentServerTick - COLUMNS_COUNT + SELECTED_COLUMN;
	}
	
	public void onDisconnect() {
		currentServerTick = -1;
		selectedTick = -1;
	}
	
	public void pause() {
		paused = !paused;
		
		if (!paused) {
			selectedTick = currentServerTick - COLUMNS_COUNT + SELECTED_COLUMN;
		}
	}
	
	public void stepForward(int amount) {
		if (paused) {
			selectedTick += amount;
		}
	}
	
	public void stepBackward(int amount) {
		if (paused) {
			selectedTick -= amount;
		}
	}
	
	private int getNamesWidth() {
		int width = 0;
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			int nameWidth = font.getWidth(meter.getName());
			
			if (nameWidth > width) {
				width = nameWidth;
			}
		}
		
		return width + 3;
	}
	
	public void render(MatrixStack matrices) {
		rowCount = client.getMeterGroup().getMeterCount();
		
		if (rowCount <= 0) {
			return;
		}
		
		updateDimensions();
		
		renderNames(matrices);
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
	
	private void updateDimensions() {
		int height = rowCount * (ROW_HEIGHT + GRID_SIZE) + 1;
		
		namesX = 0;
		namesY = 0;
		namesWidth = getNamesWidth();
		namesHeight = height;
		ticksX = namesX + namesWidth;
		ticksY = 0;
		ticksWidth = COLUMNS_COUNT * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
		ticksHeight = height;
		subTicksX = ticksX + ticksWidth + TICKS_SUBTICKS_GAP;
		subTicksY = 0;
		subTicksWidth = 0;
		subTicksHeight = height;
	}
	
	private void renderNames(MatrixStack matrices) {
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
		
		List<Meter> meters = client.getMeterGroup().getMeters();
		long firstTick = selectedTick - SELECTED_COLUMN;
		
		for (int i = 0; i < rowCount; i++) {
			Meter meter = meters.get(i);
			MeterLogs logs = meter.getLogs();
			
			int column = COLUMNS_COUNT;
			int y = ticksY + i * (ROW_HEIGHT + GRID_SIZE);
			boolean active;
			
			LogEntry<?> log = logs.getLastLogBefore(firstTick + column + 1, LogType.ACTIVE);
			
			if (log == null) {
				int j = 0;
				log = logs.getLog(j++);
				
				while (log != null && log.getType() != LogType.ACTIVE) {
					log = logs.getLog(j++);
				}
				
				if (log != null && log.getType() == LogType.ACTIVE) {
					active = !(boolean)log.get();
				} else {
					log = null;
					active = meter.isActive();
				}
			} else {
				active = (boolean)log.get();
			}
			
			while (column > 0) {
				long tick = firstTick + column--;
				
				if (log != null && log.getTick() > tick) {
					log = logs.getLastLogBefore(tick + 1, LogType.ACTIVE);
					
					if (log == null) {
						active = !active;
					} else {
						active = (boolean)log.get();
					}
				}
				
				if (active) {
					int x = ticksX + column * (COLUMN_WIDTH + GRID_SIZE);
					
					fill(matrices, x, y, x + COLUMN_WIDTH + GRID_SIZE, y + ROW_HEIGHT + GRID_SIZE, meter.getColor());
				}
			}
		}
		
		drawGridLines(matrices, ticksX, ticksY, COLUMNS_COUNT, rowCount);
		drawSelectedTickIndicator(matrices);
	}
	
	private void renderSubticksTable(MatrixStack matrices) {
		drawBackground(matrices, subTicksX, subTicksY, subTicksX + subTicksWidth, subTicksY + subTicksHeight);
		
		
	}
	
	private void drawBackground(MatrixStack matrices, int x, int y, int width, int height) {
		fill(matrices, x, y, width, height, BACKGROUND_COLOR);
	}
	
	private void drawLogEntry(MatrixStack matrices, LogEntry<?> log, int x, int y, int color, boolean transition) {
		LogType<?> type = log.getType();
		
		if (type == LogType.POWERED) {
			int half = ROW_HEIGHT / 2;
			
			if (transition) {
				if ((boolean)log.get()) {
					fill(matrices, x, y, x + COLUMN_WIDTH, y + half, BACKGROUND_COLOR);
					fill(matrices, x + 1, y + 1, x + COLUMN_WIDTH - 1, y + half, color);					
				} else {
					fill(matrices, x, y, x + COLUMN_WIDTH, y + half, color);
					fill(matrices, x + 1, y + 1, x + COLUMN_WIDTH - 1, y + half, BACKGROUND_COLOR);		
				}
			} else {
				fill(matrices, x, y, x + COLUMN_WIDTH, y + half, color);
			}
		} else if (type == LogType.ACTIVE) {
			int half = ROW_HEIGHT / 2;
			y = y + ROW_HEIGHT - half;
			
			if (transition) {
				if ((boolean)log.get()) {
					fill(matrices, x, y, x + COLUMN_WIDTH, y + half, BACKGROUND_COLOR);
					fill(matrices, x + 1, y - 1, x + COLUMN_WIDTH - 1, y + half, color);					
				} else {
					fill(matrices, x, y, x + COLUMN_WIDTH, y + half, color);
					fill(matrices, x + 1, y - 1, x + COLUMN_WIDTH - 1, y + half, BACKGROUND_COLOR);		
				}
			} else {
				fill(matrices, x, y, x + COLUMN_WIDTH, y + half, color);
			}
		} else if (type == LogType.MOVED) {
			if (transition) {
				int half = ROW_HEIGHT / 2;
				
				
			}
		}
	}
	
	private void drawLogTypeNormal(MatrixStack matrices, LogType<?> type, int x, int y, int color) {
		if (type == LogType.POWERED) {
			int half = ROW_HEIGHT / 2;
			
			fill(matrices, x, y, x + COLUMN_WIDTH, y + half, color);
		} else if (type == LogType.ACTIVE) {
			int half = ROW_HEIGHT / 2;
			y = y + ROW_HEIGHT - half;
			
			fill(matrices, x, y, x + COLUMN_WIDTH, y + half, color);
		} else if (type == LogType.MOVED) {
			
		}
	}
	
	private <T extends LogEntry<?>> void drawLogTypeTransition(MatrixStack matrices, LogType<T> type, int x, int y, int color, boolean in) {
		if (type == LogType.POWERED) {
			int half = ROW_HEIGHT / 2;
			
			if (in) {
				fill(matrices, x, y, x + COLUMN_WIDTH, y + half, BACKGROUND_COLOR);
				fill(matrices, x + 1, y + 1, x + COLUMN_WIDTH - 1, y + half, color);
			} else {
				fill(matrices, x, y, x + COLUMN_WIDTH, y + half, color);
				fill(matrices, x + 1, y + 1, x + COLUMN_WIDTH - 1, y + half, BACKGROUND_COLOR);
			}
		} else if (type == LogType.ACTIVE) {
			
		} else if (type == LogType.MOVED) {
			
		}
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
		int bottom = y + rowCount * (ROW_HEIGHT + GRID_SIZE);
		
		fill(matrices, left            , top            , left + GRID_SIZE , bottom            , SELECTED_TICK_COLOR); // left
		fill(matrices, left + GRID_SIZE, top            , right + GRID_SIZE, top + GRID_SIZE   , SELECTED_TICK_COLOR); // top
		fill(matrices, right           , top + GRID_SIZE, right + GRID_SIZE, bottom + GRID_SIZE, SELECTED_TICK_COLOR); // right
		fill(matrices, left            , bottom         , right            , bottom + GRID_SIZE, SELECTED_TICK_COLOR); // bottom
	}
}
