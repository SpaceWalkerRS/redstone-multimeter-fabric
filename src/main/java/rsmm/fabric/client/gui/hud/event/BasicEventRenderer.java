package rsmm.fabric.client.gui.hud.event;

import static rsmm.fabric.client.gui.HudSettings.COLUMN_WIDTH;
import static rsmm.fabric.client.gui.HudSettings.GRID_SIZE;
import static rsmm.fabric.client.gui.HudSettings.ROW_HEIGHT;
import static rsmm.fabric.client.gui.HudSettings.columnCount;

import java.util.function.BiFunction;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.gui.hud.MultimeterHud;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;

public class BasicEventRenderer extends MeterEventRenderer {
	
	protected final BiFunction<Meter, MeterEvent, Integer> edgeColorProvider;
	protected final BiFunction<Meter, MeterEvent, Integer> centerColorProvider;
	
	public BasicEventRenderer(MultimeterHud hud) {
		this(hud, (m, e) -> hud.settings.colorBackground, (m, e) -> m.getColor());
	}
	
	public BasicEventRenderer(MultimeterHud hud, BiFunction<Meter, MeterEvent, Integer> edgeColorProvider, BiFunction<Meter, MeterEvent, Integer> centerColorProvider) {
		super(hud, null);
		
		this.edgeColorProvider = edgeColorProvider;
		this.centerColorProvider = centerColorProvider;
	}
	
	@Override
	public void renderTickLogs(MatrixStack matrices, int x, int y, long firstTick, long lastTick,  Meter meter) {
		y += GRID_SIZE;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		long lastHudTick = firstTick + columnCount();
		
		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}
		
		while (event.isBefore(lastHudTick)) {
			int column = (int)(event.getTick() - firstTick); // The event is no older than 1M ticks, so we can safely cast to int
			int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
			
			drawEvent(matrices, columnX, y, meter, event);
			
			if ((event = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderSubtickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter) {
		y += GRID_SIZE;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		while (event.isBefore(tick, subTickCount)) {
			int column = event.getSubTick();
			int columnX = x + column * (COLUMN_WIDTH + GRID_SIZE) + GRID_SIZE;
			
			drawEvent(matrices, columnX, y, meter, event);
			
			if ((event = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	public void setType(EventType type) {
		this.type = type;
	}
	
	protected void drawEvent(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		matrices.push();
		drawCenter(matrices, x, y, meter ,event);
		matrices.translate(0, 0, -0.01);
		drawEdges(matrices, x, y, meter ,event);
		matrices.pop();
	}
	
	protected void drawEdges(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int half = ROW_HEIGHT / 2;
		int color = edgeColorProvider.apply(meter, event);
		
		drawRect(hud, matrices, x, y + half - 1, x + COLUMN_WIDTH, y + ROW_HEIGHT - half + 1, color);
	}
	
	protected void drawCenter(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int half = ROW_HEIGHT / 2;
		int color = centerColorProvider.apply(meter, event);
		
		drawRect(hud, matrices, x, y + half, x + COLUMN_WIDTH, y + ROW_HEIGHT - half, color);
	}
}
