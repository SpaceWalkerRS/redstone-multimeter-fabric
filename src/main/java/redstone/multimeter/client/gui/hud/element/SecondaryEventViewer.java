package redstone.multimeter.client.gui.hud.element;

import net.minecraft.client.gui.GuiGraphics;

import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.MeterLogs;

public class SecondaryEventViewer extends MeterEventViewer {

	public SecondaryEventViewer(MultimeterHud hud) {
		super(hud);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		if (hud.isPaused() && getColumnCount() > 0) {
			super.render(graphics, mouseX, mouseY);
		}
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		if (isHovered(mouseX, mouseY)) {
			int row = hud.getHoveredRow(mouseY);
			Meter meter = hud.meters.get(row);
			long tick = hud.getSelectedTick();
			int subtick = getHoveredColumn(mouseX);

			MeterLogs logs = meter.getLogs();
			EventLog log = logs.getLogAt(tick, subtick);

			if (log != null && meter.isMetering(log.getEvent().getType())) {
				return log.getTooltip();
			}
		}

		return super.getTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawHighlights(GuiGraphics graphics, int mouseX, int mouseY) {
		if (isHovered(mouseX, mouseY)) {
			int column = getHoveredColumn(mouseX);
			int row = hud.getHoveredRow(mouseY);

			drawHighlight(graphics, column, 1, row, 1, false);
		}
	}

	@Override
	protected void drawDecorators(GuiGraphics graphics) {

	}

	@Override
	protected void drawMeterEvents(GuiGraphics graphics) {
		long tick = hud.getSelectedTick();
		int subticks = hud.client.getMeterGroup().getLogManager().getSubtickCount(tick);

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderSubtickLogs(graphics, x, y, tick, subticks, meter);
		});
	}

	@Override
	protected int getColumnCount() {
		if (hud.isPaused()) {
			return hud.client.getMeterGroup().getLogManager().getSubtickCount(hud.getSelectedTick());
		}

		return 0;
	}
}
