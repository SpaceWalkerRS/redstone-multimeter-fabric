package rsmm.fabric.client.gui.hud.event;

import java.util.function.BiFunction;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.gui.hud.MultimeterHud;
import rsmm.fabric.client.option.Options;
import rsmm.fabric.common.meter.Meter;
import rsmm.fabric.common.meter.event.EventType;
import rsmm.fabric.common.meter.event.MeterEvent;
import rsmm.fabric.common.meter.log.MeterLogs;

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
		y += hud.settings.gridSize;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		long lastHudTick = firstTick + Options.HUD.COLUMN_COUNT.get();
		
		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}
		
		long tick = -1;
		
		while (event.isBefore(lastHudTick)) {
			if (event.isAfter(tick)) {
				tick = event.getTick();
				
				int column = (int)(tick - firstTick); // The event is no older than 1M ticks, so we can safely cast to int
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
				
				drawEvent(matrices, columnX, y, meter, event);
			}
			
			if ((event = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderSubtickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter) {
		y += hud.settings.gridSize;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick) + 1;
		MeterEvent event = logs.getLog(type, index);
		
		if (event == null) {
			return;
		}
		
		while (event.isBefore(tick, subTickCount)) {
			int column = event.getSubTick();
			int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
			
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
		int width = hud.settings.columnWidth;
		int half = hud.settings.rowHeight / 2;
		int height = (2 * half < hud.settings.rowHeight) ? 3 : 4;
		int color = edgeColorProvider.apply(meter, event);
		
		hud.renderer.drawRect(matrices, x, y + half - 1, width, height, color);
	}
	
	protected void drawCenter(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int width = hud.settings.columnWidth;
		int half = hud.settings.rowHeight / 2;
		int height = (2 * half < hud.settings.rowHeight) ? 1 : 2;
		int color = centerColorProvider.apply(meter, event);
		
		hud.renderer.drawRect(matrices, x, y + half, width, height, color);
	}
}
