package redstone.multimeter.client.gui.hud.element;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.MeterLogs;

public class SecondaryEventViewer extends MeterEventViewer {

	public SecondaryEventViewer(MultimeterHud hud) {
		super(hud);
	}

	@Override
	public void render(GuiRenderer renderer, int mouseX, int mouseY) {
		if (!hud.client.isPreviewing() && hud.isPaused() && getColumnCount() > 0) {
			super.render(renderer, mouseX, mouseY);
		}
	}

	@Override
	public void mouseMove(double mouseX, double mouseY) {
	}

	@Override
	public Tooltip getTooltip(int mouseX, int mouseY) {
		if (!hud.isFocusMode() && isMouseOver(mouseX, mouseY)) {
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
	protected void drawHighlights(GuiRenderer renderer, int mouseX, int mouseY) {
		if (hud.isFocusMode()) {
			EventLog highlight = hud.getFocussedEvent();

			if (highlight != null) {
				drawHighlight(renderer, highlight.getSubtick(), 1, 0, hud.meters.size(), true);
			}
		}
		if (isMouseOver(mouseX, mouseY)) {
			int column = getHoveredColumn(mouseX);
			int row = hud.getHoveredRow(mouseY);

			drawHighlight(renderer, column, 1, row, 1, false);
		}
	}

	@Override
	protected void drawDecorators(GuiRenderer renderer) {
	}

	@Override
	protected void drawMeterEvents(GuiRenderer renderer) {
		long tick = hud.getSelectedTick();
		int subticks = hud.client.getMeterGroup().getLogManager().getSubtickCount(tick);

		drawMeterLogs((x, y, meter) -> {
			hud.eventRenderers.renderSubtickLogs(renderer, x, y, tick, subticks, meter);
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
