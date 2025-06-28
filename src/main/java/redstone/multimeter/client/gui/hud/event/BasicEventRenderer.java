package redstone.multimeter.client.gui.hud.event;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.MeterLogs;

public class BasicEventRenderer extends MeterEventRenderer {

	public BasicEventRenderer(MultimeterHud hud) {
		super(hud, null);
	}

	@Override
	public void renderTickLogs(GuiRenderer renderer, int x, int y, long firstTick, long lastTick, Meter meter) {
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

				drawEvent(renderer, columnX, y, meter, log.getEvent());
			}

			if ((log = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}

	@Override
	public void renderSubtickLogs(GuiRenderer renderer, int x, int y, long tick, int subtickCount, Meter meter) {
		y += hud.settings.gridSize;

		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick) + 1;
		EventLog log = logs.getLog(type, index);

		if (log == null) {
			return;
		}

		while (log.isBefore(tick, subtickCount)) {
			int column = log.getSubtick();
			int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;

			drawEvent(renderer, columnX, y, meter, log.getEvent());

			if ((log = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}

	public void setType(EventType type) {
		this.type = type;
	}

	protected void drawEvent(GuiRenderer renderer, int x, int y, Meter meter, MeterEvent event) {
		renderer.push();
		drawCenter(renderer, x, y, meter, event);
		renderer.translate(0, 0, -0.01);
		drawEdges(renderer, x, y, meter, event);
		renderer.pop();
	}

	protected void drawEdges(GuiRenderer renderer, int x, int y, Meter meter, MeterEvent event) {
		int height = hud.settings.scale * (3 + hud.settings.hparity);

		if (hud.settings.rowHeight < height) {
			return;
		}

		int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;

		int x0 = x;
		int y0 = y + heightOffset;
		int x1 = x0 + hud.settings.columnWidth;
		int y1 = y0 + height;
		int color = hud.settings.colorBackground;

		renderer.fill(x0, y0, x1, y1, color);
	}

	protected void drawCenter(GuiRenderer renderer, int x, int y, Meter meter, MeterEvent event) {
		int height = hud.settings.scale * (1 + hud.settings.hparity);
		int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;

		int x0 = x;
		int y0 = y + heightOffset;
		int x1 = x0 + hud.settings.columnWidth;
		int y1 = y0 + height;
		int color = meter.getColor();

		renderer.fill(x0, y0, x1, y1, color);
	}
}
