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
	
	private static final int BORDER = 1;
	private static final int TICKS_SUBTICKS_GAP = 3; // Space between the ticks table and the subticks table
	
	private static final int COLUMN_WIDTH = 4; // Width of a column in the ticks and subticks tables
	private final int ROW_HEIGHT; // Height of a row
	private static final int COLUMN_GAP = 1; // Space between each column in the ticks and subticks tables
	private static final int ROW_GAP = 1; // Space between each row
	
	private static final int COLUMNS_COUNT = 60;
	private static final int SELECTED_COLUMN = 45;
	
	private static final int BACKGROUND_COLOR = 0xFF202020;
	private static final int MAJOR_GRID_COLOR = 0xFF606060;
	private static final int MINOR_GRID_COLOR = 0xFF404040;
	private static final int SELECTED_TICK_COLOR = 0xFFFFFFFF;
	
	private static final int POWERED_TEXT_COLOR = 0xFF000000;
	private static final int UNPOWERED_TEXT_COLOR = 0xFF707070;
	private static final int METER_NAME_COLOR = 0xFFFFFFFF;
	private static final int PAUSED_TEXT_COLOR = 0xFF000000;
	
	private final MultimeterClient client;
	private final TextRenderer font;
	
	private long currentServerTick = -1;
	
	private int namesX = BORDER;
	private int namesY = BORDER;
	private int namesWidth;
	private int namesHeight;
	private int ticksX;
	private int ticksY = BORDER;
	private int ticksWidth;
	private int ticksHeight;
	private int subTicksX;
	private int subTicksY = BORDER;
	private int subTicksWidth;
	private int subTicksHeight;
	
	private int rowCount;
	
	private boolean paused;
	
	public MultimeterHudRenderer(MultimeterClient client) {
		MinecraftClient mcClient = client.getMinecraftClient();
		
		this.client = client;
		this.font = mcClient.textRenderer;
		
		ROW_HEIGHT = font.fontHeight;
	}
	
	public void tick() {
		currentServerTick++;
	}
	
	public void syncTime(long serverTick) {
		currentServerTick = serverTick;
	}
	
	public void onDisconnect() {
		currentServerTick = -1;
	}
	
	public void pause() {
		paused = !paused;
	}
	
	public void stepForward(int amount) {
		
	}
	
	public void stepBackward(int amount) {
		
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
		
		setDimensions();
		
		renderNames(matrices);
		renderTicksTable(matrices);
		
		if (paused) {
			renderSubticksTable(matrices);
		}
		
		String text = client.getMeterGroup().getName();
		
		if (paused) {
			text += " (Paused)";
		}
		
		font.draw(matrices, text, namesX + 1, namesY + namesHeight + 2, PAUSED_TEXT_COLOR);
	}
	
	private void setDimensions() {
		int height = rowCount * (ROW_HEIGHT + ROW_GAP) + 1;
		
		namesX = BORDER;
		namesY = BORDER;
		namesWidth = getNamesWidth();
		namesHeight = height;
		ticksX = namesX + namesWidth;
		ticksY = BORDER;
		ticksWidth = COLUMNS_COUNT * (COLUMN_WIDTH + COLUMN_GAP) + 1;
		ticksHeight = height;
		subTicksX = ticksX + ticksWidth + TICKS_SUBTICKS_GAP;
		subTicksY = BORDER;
		subTicksWidth = 0;
		subTicksHeight = height;
	}
	
	private void renderNames(MatrixStack matrices) {
		fill(matrices, namesX, namesY, namesX + namesWidth, namesY + namesHeight, BACKGROUND_COLOR);
		
		int x = namesX + 1;
		int y = namesY + 2;
		for (Meter meter : client.getMeterGroup().getMeters()) {
			font.draw(matrices, meter.getName(), x, y, METER_NAME_COLOR);
			
			y += ROW_HEIGHT + ROW_GAP;
		}
	}
	
	private void renderTicksTable(MatrixStack matrices) {
		fill(matrices, ticksX, ticksY, ticksX + ticksWidth, ticksY + ticksHeight, BACKGROUND_COLOR);
		
		List<Meter> meters = client.getMeterGroup().getMeters();
		for (int i = 0; i < rowCount; i++) {
			Meter meter = meters.get(i);
			MeterLogs logs = meter.getLogs();
			long firstTick = currentServerTick - COLUMNS_COUNT;
			
			int column = COLUMNS_COUNT;
			int y = ticksY + i * (ROW_HEIGHT + ROW_GAP);
			
			while (column > 0) {
				long tick = firstTick + column;
				LogEntry<?> log = logs.getLastLogBefore(tick, LogType.ACTIVE);
				
				if (log == null) {
					log = logs.getLog(0);
					
					boolean active;
					if (log == null || log.getType() != LogType.ACTIVE) {
						active = meter.isActive();
					} else {
						active = (boolean)log.get();
					}
					
					while (column-- > 0) {
						int x = ticksX + column * (COLUMN_WIDTH + COLUMN_GAP);
						
						fill(matrices, x, y, x + COLUMN_WIDTH + COLUMN_GAP, y + ROW_HEIGHT + ROW_GAP, meter.getColor());
					}
				} else {
					boolean active = (boolean)log.get();
					long stop = log.getTick() - firstTick;
					
					while (column-- > stop && column >= 0) {
						if (!active) {
							continue;
						}
						int x = ticksX + column * (COLUMN_WIDTH + COLUMN_GAP);
						
						fill(matrices, x, y, x + COLUMN_WIDTH + COLUMN_GAP, y + ROW_HEIGHT + ROW_GAP, meter.getColor());
					}
				}
			}
		}
		
		for (int i = 0; i <= rowCount; i++) {
			int y = ticksY + i * (ROW_HEIGHT + ROW_GAP);
			
			fill(matrices, ticksX, y, ticksX + ticksWidth, y + 1, MINOR_GRID_COLOR);
		}
		for (int i = 0; i <= COLUMNS_COUNT; i++) {
			int x = ticksX + i * (COLUMN_WIDTH + COLUMN_GAP);
			int color = i % 5 == 0 ? MAJOR_GRID_COLOR : MINOR_GRID_COLOR;
			
			fill(matrices, x, ticksY, x + 1, ticksY + ticksHeight, color);
		}
		
	}
	
	private void renderSubticksTable(MatrixStack matrices) {
		fill(matrices, subTicksX, subTicksY, subTicksX + subTicksWidth, subTicksY + subTicksHeight, BACKGROUND_COLOR);
		
	}
}
