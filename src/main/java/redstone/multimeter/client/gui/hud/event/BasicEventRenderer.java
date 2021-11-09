package redstone.multimeter.client.gui.hud.event;

import java.util.function.BiFunction;

import net.minecraft.client.util.math.MatrixStack;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.MeterLogs;

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
		EventLog log = logs.getLog(type, index);
		
		if (log == null) {
			return;
		}
		
		long lastHudTick = firstTick + Options.HUD.COLUMN_COUNT.get();
		
		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}
		
		long tick = -1;
		
		while (log.isBefore(lastHudTick)) {
			if (log.isAfter(tick)) {
				tick = log.getTick();
				
				int column = (int)(tick - firstTick); // The event is no older than 1M ticks, so we can safely cast to int
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
				
				drawEvent(matrices, columnX, y, meter, log.getEvent());
			}
			
			if ((log = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}
	
	@Override
	public void renderSubtickLogs(MatrixStack matrices, int x, int y, long tick, int subTickCount, Meter meter) {
		y += hud.settings.gridSize;
		
		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick) + 1;
		EventLog log = logs.getLog(type, index);
		
		if (log == null) {
			return;
		}
		
		while (log.isBefore(tick, subTickCount)) {
			int column = log.getSubtick();
			int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
			
			drawEvent(matrices, columnX, y, meter, log.getEvent());
			
			if ((log = logs.getLog(type, ++index)) == null) {
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
		
		hud.renderer.renderRect(matrices, x, y + half - 1, width, height, color);
	}
	
	protected void drawCenter(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int width = hud.settings.columnWidth;
		int half = hud.settings.rowHeight / 2;
		int height = (2 * half < hud.settings.rowHeight) ? 1 : 2;
		int color = centerColorProvider.apply(meter, event);
		
		hud.renderer.renderRect(matrices, x, y + half, width, height, color);
	}
}
