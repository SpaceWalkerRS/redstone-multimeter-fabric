package redstone.multimeter.client.gui.hud.event;

import net.minecraft.client.gui.GuiGraphics;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;

public class ScheduledEventRenderer extends BasicEventRenderer {

	public ScheduledEventRenderer(MultimeterHud hud, EventType type) {
		super(hud);

		this.setType(type);
	}

	@Override
	protected void drawEdges(GuiGraphics graphics, int x, int y, Meter meter, MeterEvent event) {
		if (scheduling(event)) {
			if (hud.settings.columnWidth < 3) {
				return;
			}

			int width = hud.settings.columnWidth;
			int half = hud.settings.rowHeight / 2;
			int height = (2 * half < hud.settings.rowHeight) ? 5 : 6;
			int color = edgeColorProvider.apply(meter, event);

			hud.renderer.renderRect(graphics, x, y + half - (height / 2), width, height, color);
		} else {
			super.drawEdges(graphics, x, y, meter, event);
		}
	}

	@Override
	protected void drawCenter(GuiGraphics graphics, int x, int y, Meter meter, MeterEvent event) {
		if (scheduling(event)) {
			int halfWidth = hud.settings.columnWidth / 2;
			int width = hud.settings.columnWidth - 2;
			int halfHeight = hud.settings.rowHeight / 2;
			int height = (2 * halfHeight < hud.settings.rowHeight) ? 3 : 4;
			int color = centerColorProvider.apply(meter, event);

			hud.renderer.renderRect(graphics, x + halfWidth - (width / 2), y + halfHeight - (height / 2), width, height, color);
		} else {
			super.drawCenter(graphics, x, y, meter, event);
		}
	}

	protected boolean scheduling(MeterEvent event) {
		return (event.getMetadata() >> 30) == 1;
	}
}
