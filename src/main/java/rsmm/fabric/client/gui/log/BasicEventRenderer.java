package rsmm.fabric.client.gui.log;

import static rsmm.fabric.client.gui.HudSettings.*;

import java.util.function.BiFunction;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.MeterLogs;

public class BasicEventRenderer extends MeterEventRenderer {
	
	protected final BiFunction<Meter, MeterEvent, Integer> edgeColorProvider;
	protected final BiFunction<Meter, MeterEvent, Integer> centerColorProvider;
	
	public BasicEventRenderer() {
		this((m, e) -> BACKGROUND_COLOR, (m, e) -> m.getColor());
	}
	
	public BasicEventRenderer(BiFunction<Meter, MeterEvent, Integer> edgeColorProvider, BiFunction<Meter, MeterEvent, Integer> centerColorProvider) {
		super(null);
		
		this.edgeColorProvider = edgeColorProvider;
		this.centerColorProvider = centerColorProvider;
	}
	
	@Override
	public void renderTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long firstTick, long lastTick,  Meter meter) {
		y += GRID_SIZE;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		long lastHudTick = firstTick + COLUMN_COUNT;
		
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
	public void renderSubTickLogs(MatrixStack matrices, TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter) {
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
		drawEdges(matrices, x, y, meter ,event);
		drawCenter(matrices, x, y, meter ,event);
	}
	
	protected void drawEdges(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int half = ROW_HEIGHT / 2;
		int color = edgeColorProvider.apply(meter, event);
		
		fill(matrices, x, y + half - 1, x + COLUMN_WIDTH, y + ROW_HEIGHT - half + 1, color);
	}
	
	protected void drawCenter(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int half = ROW_HEIGHT / 2;
		int color = centerColorProvider.apply(meter, event);
		
		fill(matrices, x, y + half, x + COLUMN_WIDTH, y + ROW_HEIGHT - half, color);
	}
}
