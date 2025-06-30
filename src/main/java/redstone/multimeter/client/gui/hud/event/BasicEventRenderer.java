package redstone.multimeter.client.gui.hud.event;

import com.mojang.blaze3d.vertex.PoseStack;

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
	public void renderTickLogs(PoseStack poses, int x, int y, long firstTick, long lastTick, Meter meter) {
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

				drawEvent(poses, columnX, y, meter, log.getEvent());
			}

			if ((log = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}

	@Override
	public void renderSubtickLogs(PoseStack poses, int x, int y, long tick, int subtickCount, Meter meter) {
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

			drawEvent(poses, columnX, y, meter, log.getEvent());

			if ((log = logs.getLog(type, ++index)) == null) {
				break;
			}
		}
	}

	public void setType(EventType type) {
		this.type = type;
	}

	protected void drawEvent(PoseStack poses, int x, int y, Meter meter, MeterEvent event) {
		poses.pushPose();
		drawCenter(poses, x, y, meter, event);
		poses.translate(0, 0, -0.01);
		drawEdges(poses, x, y, meter, event);
		poses.popPose();
	}

	protected void drawEdges(PoseStack poses, int x, int y, Meter meter, MeterEvent event) {
		if (hud.settings.rowHeight < (3 + hud.settings.hparity) * hud.settings.scale) {
			return;
		}

		int width = hud.settings.columnWidth;
		int height = hud.settings.scale * (3 + hud.settings.hparity);
		int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;
		int color = hud.settings.colorBackground;

		hud.renderer.renderRect(poses, x, y + heightOffset, width, height, color);
	}

	protected void drawCenter(PoseStack poses, int x, int y, Meter meter, MeterEvent event) {
		int width = hud.settings.columnWidth;
		int height = hud.settings.scale * (1 + hud.settings.hparity);
		int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;
		int color = meter.getColor();

		hud.renderer.renderRect(poses, x, y + heightOffset, width, height, color);
	}
}
