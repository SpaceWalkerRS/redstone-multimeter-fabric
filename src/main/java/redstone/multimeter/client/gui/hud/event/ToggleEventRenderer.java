package redstone.multimeter.client.gui.hud.event;

import com.mojang.blaze3d.vertex.PoseStack;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.option.Options;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.MeterLogs;

public abstract class ToggleEventRenderer extends MeterEventRenderer {

	protected Mode mode;

	protected ToggleEventRenderer(MultimeterHud client, EventType type) {
		super(client, type);
	}

	@Override
	public void renderTickLogs(PoseStack poses, int x, int y, long firstTick, long lastTick, Meter meter) {
		updateMode(meter);

		y += hud.settings.gridSize;
		int color = meter.getColor();

		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick);
		EventLog log = logs.getLog(type, index);
		EventLog nextLog = logs.getLog(type, ++index);

		long lastHudTick = firstTick + Options.HUD.COLUMN_COUNT.get();

		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}

		if (nextLog == null) {
			if (isToggled(meter)) {
				draw(poses, x + hud.settings.gridSize, y, color, (int)(lastHudTick - firstTick));
			}

			return;
		}

		long currentTick = -1;

		while (log == null || log.isBefore(lastHudTick)) {
			boolean isLogInTable = (log != null && !log.isBefore(firstTick));
			boolean isNextLogInTable = (nextLog != null && nextLog.isBefore(lastHudTick));

			if (isLogInTable && log.getTick() != currentTick) {
				currentTick = log.getTick();

				int column = (int)(log.getTick() - firstTick);
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;

				if (wasToggled(log)) {
					drawOn(poses, columnX, y, color);
				} else {
					drawOff(poses, columnX, y, color);
				}
			}

			long start = isLogInTable ? log.getTick() + 1 : firstTick;
			long end = isNextLogInTable ? nextLog.getTick() : lastHudTick;

			if (log == null ? !wasToggled(nextLog) : wasToggled(log)) {
				int column = (int)(start - firstTick);
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;

				draw(poses, columnX, y, color, (int)(end - start));
			}

			do {
				log = nextLog;
				nextLog = logs.getLog(type, ++index);
			} while (nextLog != null && nextLog.getTick() == currentTick);

			if (log == null) {
				break;
			}
		}
	}

	@Override
	public void renderPulseLengths(PoseStack poses, int x, int y, long firstTick, long lastTick, Meter meter) {
		updateMode(meter);

		if (mode != Mode.ALL) {
			return;
		}

		y += hud.settings.gridSize;
		int textY = y + hud.settings.rowHeight - (hud.settings.rowHeight + hud.font.lineHeight) / 2;
		int color = meter.getColor();

		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, firstTick);
		EventLog log = logs.getLog(type, index);
		EventLog nextLog = logs.getLog(type, ++index);

		if (nextLog == null) {
			return;
		}

		long lastHudTick = firstTick + Options.HUD.COLUMN_COUNT.get();

		if (lastHudTick > lastTick) {
			lastHudTick = lastTick;
		}

		long currentTick = -1;

		while (log == null || log.isBefore(lastHudTick)) {
			boolean isLogInTable = (log != null && !log.isBefore(firstTick));
			boolean isNextLogInTable = (nextLog != null && nextLog.isBefore(lastHudTick));

			long start = isLogInTable ? log.getTick() + 1 : firstTick;
			long end = isNextLogInTable ? nextLog.getTick() : lastHudTick;

			if (log != null && nextLog != null) {
				long pulseLength = nextLog.getTick() - log.getTick();

				if (pulseLength > 5) {
					int startX = x + (int)(start - firstTick) * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;
					int endX = x + (int)(end - firstTick) * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;

					String text = String.valueOf(pulseLength);

					int availableWidth = endX - startX;
					int requiredWidth = hud.font.width(text) + 1;

					if (requiredWidth < availableWidth) {
						boolean toggled = wasToggled(log);

						int bgColor = toggled ? color : hud.settings.colorBackground;
						int textColor = toggled ? hud.settings.colorTextOn : hud.settings.colorTextOff;

						poses.pushPose();
						hud.renderer.renderText(poses, text, startX + 1, textY + 1, textColor);
						poses.translate(0, 0, -0.01);
						hud.renderer.renderRect(poses, startX, y, requiredWidth, hud.settings.rowHeight, bgColor);
						poses.popPose();
					}
				}
			}

			do {
				log = nextLog;
				nextLog = logs.getLog(type, ++index);
			} while (nextLog != null && nextLog.getTick() == currentTick);

			if (log == null) {
				break;
			}
		}
	}

	@Override
	public void renderSubtickLogs(PoseStack poses, int x, int y, long tick, int subtickCount, Meter meter) {
		updateMode(meter);

		y += hud.settings.gridSize;
		int color = meter.getColor();

		MeterLogs logs = meter.getLogs();
		int index = logs.getLastLogBefore(type, tick);
		EventLog log = logs.getLog(type, index);
		EventLog nextLog = logs.getLog(type, ++index);

		if (nextLog == null) {
			if (isToggled(meter)) {
				draw(poses, x + hud.settings.gridSize, y, color, subtickCount);
			}

			return;
		}

		while (log == null || log.isBefore(tick, subtickCount)) {
			boolean isLogInTable = (log != null && log.isAt(tick));
			boolean isNextLogInTable = (nextLog != null && nextLog.isAt(tick));

			if (isLogInTable) {
				int column = log.getSubtick();
				int columnX = x + column * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;

				if (wasToggled(log)) {
					drawOn(poses, columnX, y, color);
				} else {
					drawOff(poses, columnX, y, color);
				}
			}

			int start = isLogInTable ? log.getSubtick() + 1 : 0;
			int end = isNextLogInTable ? nextLog.getSubtick() : subtickCount;

			if (log == null ? !wasToggled(nextLog) : wasToggled(log)) {
				int columnX = x + start * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize;

				draw(poses, columnX, y, color, end - start);
			}

			log = nextLog;
			nextLog = logs.getLog(type, ++index);

			if (log == null) {
				break;
			}
		}
	}

	protected abstract void updateMode(Meter meter);

	private boolean wasToggled(EventLog log) {
		return (log.getEvent().getMetadata() & 1) != 0;
	}

	protected abstract boolean isToggled(Meter meter);

	private void draw(PoseStack poses, int x, int y, int color) {
		int width = hud.settings.columnWidth;
		int height = hud.settings.rowHeight;

		if (mode != Mode.ALL) {
			height = height - (height / 2); // round up

			if (mode == Mode.BOTTOM) {
				y += (hud.settings.rowHeight - height);
			}
		}

		hud.renderer.renderRect(poses, x, y, width, height, color);
	}

	private void draw(PoseStack poses, int x, int y, int color, int count) {
		for (int i = 0; i < count; i++) {
			draw(poses, x + i * (hud.settings.columnWidth + hud.settings.gridSize), y, color);
		}
	}

	private void drawOn(PoseStack poses, int x, int y, int color) {
		x += hud.settings.scale;
		y += hud.settings.scale;
		int width = hud.settings.columnWidth - 2 * hud.settings.scale;
		int height = hud.settings.rowHeight - 2 * hud.settings.scale;

		if (mode != Mode.ALL) {
			height /= 2;

			if (mode == Mode.BOTTOM) {
				y += (hud.settings.rowHeight - (height + 2 * hud.settings.scale));
			}
		}

		hud.renderer.renderRect(poses, x, y, width, height, color);
	}

	private void drawOff(PoseStack poses, int x, int y, int color) {
		poses.pushPose();
		drawOn(poses, x, y, hud.settings.colorBackground);
		poses.translate(0, 0, -0.01);
		draw(poses, x, y, color);
		poses.popPose();
	}

	protected enum Mode {
		ALL, TOP, BOTTOM;
	}
}
