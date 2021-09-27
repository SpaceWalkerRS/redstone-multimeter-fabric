package rsmm.fabric.client.gui;

import static rsmm.fabric.client.gui.HudSettings.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.log.MeterEventRendererDispatcher;
import rsmm.fabric.client.listeners.HudListener;
import rsmm.fabric.client.listeners.MeterGroupListener;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;
import rsmm.fabric.util.ColorUtils;

public class MultimeterHudRenderer extends DrawableHelper implements MeterGroupListener {
	
	private final MultimeterClient client;
	private final TextRenderer font;
	private final List<Long> meterIds;
	private final MeterEventRendererDispatcher eventRenderers;
	private final Set<HudListener> listeners;
	
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
		this.meterIds = new ArrayList<>();
		this.eventRenderers = new MeterEventRendererDispatcher(this.client);
		this.listeners = new LinkedHashSet<>();
		
	}
	
	@Override
	public void meterGroupCleared(MeterGroup meterGroup) {
		updateRowCount();
	}
	
	@Override
	public void meterAdded(MeterGroup meterGroup, long id) {
		updateRowCount();
	}
	
	@Override
	public void meterRemoved(MeterGroup meterGroup, long id) {
		updateRowCount();
	}
	
	public void addListener(HudListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(HudListener listener) {
		listeners.remove(listener);
	}
	
	public void onStartup() {
		client.getMeterGroup().addMeterGroupListener(this);
	}
	
	public void onShutdown() {
		client.getMeterGroup().removeMeterGroupListener(this);
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
	
	public long getIdAtRow(int index) {
		return meterIds.get(index);
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void pause() {
		if (ROW_COUNT == 0) {
			return;
		}
		
		paused = !paused;
		
		if (!paused) {
			resetOffset();
		}
		
		listeners.forEach(listener -> listener.hudPaused());
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
	
	public void ignoreHiddenMeters(boolean ignore) {
		IGNORE_HIDDEN_METERS = ignore;
		updateRowCount();
	}
	
	public void forceFullOpacity(boolean force) {
		FORCE_FULL_OPACITY = force;
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
	
	public void render(MatrixStack matrices) {
		render(matrices, 0, 0);
	}
	
	/**
	 * Render the HUD that displays a 60 tick history
	 * of metered events in the meter group this client
	 * is subscribed to.
	 */
	public void render(MatrixStack matrices, int x, int y) {
		if (ROW_COUNT <= 0) {
			return;
		}
		
		int namesWidth = getNamesWidth();
		
		int namesTableWidth = namesTableWidth(namesWidth);
		int ticksOverviewWidth = ticksOverviewWidth();
		int height = height();
		
		renderNamesTable(matrices, x, y, namesTableWidth, height);
		renderTicksOverview(matrices, x + namesTableWidth, y, ticksOverviewWidth, height);
		if (paused) {
			renderSubTicksOverview(matrices, x + namesTableWidth + ticksOverviewWidth + TICKS_SUBTICKS_GAP, y, height);
		}
		
		font.draw(matrices, client.getMeterGroup().getName(), x + 2, y + height + 2, meterGroupNameColor(client.hasMultimeterScreenOpen()));
	}
	
	private void updateRowCount() {
		meterIds.clear();
		
		for (Meter meter : client.getMeterGroup().getMeters()) {
			if (IGNORE_HIDDEN_METERS && meter.isHidden()) {
				continue;
			}
			
			meterIds.add(meter.getId());
		}
		
		ROW_COUNT = meterIds.size();
	}
	
	public int getNamesWidth() {
		int width = 0;
		
		for (long id : meterIds) {
			Meter meter = client.getMeterGroup().getMeter(id);
			int nameWidth = font.getWidth(meter.getName());
			
			if (nameWidth > width) {
				width = nameWidth;
			}
		}
		
		return width;
	}
	
	private void renderNamesTable(MatrixStack matrices, int x, int y, int width, int height) {
		drawBackground(matrices, x, y, width, height);
		
		int nameX;
		int nameY = y + 2;
		
		for (long id : meterIds) {
			Meter meter = client.getMeterGroup().getMeter(id);
			MutableText name = new LiteralText(meter.getName());
			int nameWidth = font.getWidth(name);
			nameX = (x + width) - (nameWidth + 1);
			
			if (meter.isHidden()) {
				name.formatted(Formatting.GRAY, Formatting.ITALIC);
			}
			
			font.draw(matrices, name, nameX, nameY, ColorUtils.fromARGB(opacity(), 0xFFFFFF));
			
			nameY += ROW_HEIGHT + GRID_SIZE;
		}
	}
	
	private void renderTicksOverview(MatrixStack matrices, int x, int y, int width, int height) {
		long firstTick = getSelectedTick() - SELECTED_COLUMN;
		long currentTick = client.getLastServerTick() + 1;
		
		int markedColumn = (currentTick < firstTick || currentTick > (firstTick + COLUMN_COUNT)) ? -1 : (int)(currentTick - firstTick);
		
		drawBackground(matrices, x, y, width, height);
		drawGridLines(matrices, x, y, height, COLUMN_COUNT, markedColumn);
		
		int rowX = x;
		int rowY = y;
		
		for (long id : meterIds) {
			Meter meter = client.getMeterGroup().getMeter(id);
			eventRenderers.renderTickLogs(matrices, rowX, rowY, firstTick, currentTick, meter);
			
			rowY += ROW_HEIGHT + GRID_SIZE;
		}
		
		if (paused) {
			drawSelectedTickIndicator(matrices, x + SELECTED_COLUMN * (COLUMN_WIDTH + GRID_SIZE), y);
		}
	}
	
	private void renderSubTicksOverview(MatrixStack matrices, int x, int y, int height) {
		long selectedTick = getSelectedTick();
		int subtickCount = client.getMeterGroup().getLogManager().getSubTickCount(selectedTick);
		
		if (subtickCount <= 0) {
			return;
		}
		
		int width = subticksOverviewWidth(subtickCount);
		
		drawBackground(matrices, x, y, width, height);
		drawGridLines(matrices, x, y, height, subtickCount);
		
		int rowX = x;
		int rowY = y;
		
		for (long id : meterIds) {
			Meter meter = client.getMeterGroup().getMeter(id);
			eventRenderers.renderSubTickLogs(matrices, rowX, rowY, selectedTick, subtickCount, meter);
			
			rowY += ROW_HEIGHT + GRID_SIZE;
		}
	}
	
	private void drawBackground(MatrixStack matrices, int x, int y, int width, int height) {
		fill(matrices, x, y, x + width, y + height, backgroundColor());
	}
	
	private void drawGridLines(MatrixStack matrices, int x, int y, int height, int columnCount) {
		drawGridLines(matrices, x, y, height, columnCount, -1);
	}
	
	private void drawGridLines(MatrixStack matrices, int x, int y, int height, int columnCount, int markedColumn) {
		int width = columnCount * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
		int lineX;
		int lineY;
		int color;
		
		// Vertical lines
		for (int i = 0; i <= columnCount; i++) {
			lineX = x + i * (COLUMN_WIDTH + GRID_SIZE);
			color = (i > 0 && i < columnCount && i % 5 == 0) ? intervalGridColor() : mainGridColor();
			
			fill(matrices, lineX, y, lineX + GRID_SIZE, y + height, color);
		}
		// Horizontal lines
		for (int i = 0; i <= ROW_COUNT; i++) {
			lineY = y + i * (ROW_HEIGHT + GRID_SIZE);
			color = mainGridColor();
			
			fill(matrices, x, lineY, x + width - GRID_SIZE, lineY + GRID_SIZE, color);
		}
		// Marked column
		if (markedColumn >= 0 && markedColumn <= columnCount) {
			lineX = x + markedColumn * (COLUMN_WIDTH + GRID_SIZE);
			color = markerGridColor();
			
			fill(matrices, lineX, y + GRID_SIZE, lineX + GRID_SIZE, y + height - GRID_SIZE, color);
		}
	}
	
	private void drawSelectedTickIndicator(MatrixStack matrices, int x, int y) {
		drawSelectionIndicator(matrices, x, y, COLUMN_WIDTH + GRID_SIZE, ROW_COUNT * (ROW_HEIGHT + GRID_SIZE));
	}
	
	public void renderSelectedMeterIndicator(MatrixStack matrices, int x, int y, long selectedMeter) {
		int row = -1;
		
		for (int index = 0; index < meterIds.size(); index++) {
			long id = meterIds.get(index);
			
			if (id == selectedMeter) {
				row = index;
				break;
			}
		}
		
		if (row >= 0) {
			int namesWidth = getNamesWidth();
			int namesTableWidth = namesTableWidth(namesWidth);
			
			int indicatorX = x;
			int indicatorY = y + row * (ROW_HEIGHT + GRID_SIZE);
			int indicatorHeight = ROW_HEIGHT + GRID_SIZE;
			int indicatorWidth = namesTableWidth - GRID_SIZE;
			
			drawSelectionIndicator(matrices, indicatorX, indicatorY, indicatorWidth, indicatorHeight);
		}
	}
	
	private void drawSelectionIndicator(MatrixStack matrices, int x, int y, int width, int height) {
		int left = x;
		int right = x + width;
		int top = y;
		int bottom = y + height;
		int color = selectionIndicatorColor();
		
		fill(matrices, left            , top            , left  + GRID_SIZE, bottom            , color); // left
		fill(matrices, left + GRID_SIZE, top            , right + GRID_SIZE, top    + GRID_SIZE, color); // top
		fill(matrices, right           , top + GRID_SIZE, right + GRID_SIZE, bottom + GRID_SIZE, color); // right
		fill(matrices, left            , bottom         , right            , bottom + GRID_SIZE, color); // bottom
	}
	
	private void resetHoveredElements() {
		hoveredRow = -1;
		hoveredNameColumn = -1;
		hoveredTickColumn = -1;
		hoveredSubTickColumn = -1;
	}
	
	public void updateHoveredElements(int x, int y, double mouseX, double mouseY) {
		resetHoveredElements();
		
		if (ROW_COUNT >= 0) {
			int height = getTableHeight();
			
			if (mouseY >= y && mouseY <= (y + height)) {
				hoveredRow = (int)((mouseY - y) / (ROW_HEIGHT + GRID_SIZE));
				
				if (hoveredRow >= ROW_COUNT) {
					hoveredRow = ROW_COUNT - 1;
				}
				
				int namesWidth = getNamesWidth();
				int width = namesTableWidth(namesWidth);
				
				if (mouseX >= x && mouseX <= (x + width)) {
					hoveredNameColumn = 0;
					return;
				}
				
				if (!paused) {
					return;
				}
				
				x += width;
				width = ticksOverviewWidth();
				
				if (mouseX >= x && mouseX <= (x + width)) {
					hoveredTickColumn = (int)((mouseX - x) / (COLUMN_WIDTH + GRID_SIZE));
					
					if (hoveredTickColumn >= COLUMN_COUNT) {
						hoveredTickColumn = COLUMN_COUNT - 1;
					}
					
					return;
				}
				
				long selectedTick = getSelectedTick();
				int subtickCount = client.getMeterGroup().getLogManager().getSubTickCount(selectedTick);
				
				x += width + TICKS_SUBTICKS_GAP;
				width = subticksOverviewWidth(subtickCount);
				
				if (mouseX >= x && mouseX <= (x + width)) {
					hoveredSubTickColumn = (int)((mouseX - x) / (COLUMN_WIDTH + GRID_SIZE));
					
					if (hoveredSubTickColumn >= subtickCount) {
						hoveredSubTickColumn = subtickCount - 1;
					}
				}
			}
		}
	}
	
	public List<Text> getTextForTooltip() {
		if (hoveredRow >= 0) { 
			if (hoveredSubTickColumn >= 0) {
				long id = getIdAtRow(hoveredRow);
				Meter meter = client.getMeterGroup().getMeter(id);
				
				if (meter != null) {
					long tick = getSelectedTick();
					int subTick = hoveredSubTickColumn;
					
					MeterLogs logs = meter.getLogs();
					MeterEvent event = logs.getLastLogBefore(tick, subTick + 1);
					
					if (event != null && event.isAt(tick, subTick)) {
						return event.getTextForTooltip();
					}
				}
			}
		}
		
		return Collections.emptyList();
	}
}
