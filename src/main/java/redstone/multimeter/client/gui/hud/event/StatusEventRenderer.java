package redstone.multimeter.client.gui.hud.event;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;

public abstract class StatusEventRenderer extends BasicEventRenderer {

	public StatusEventRenderer(MultimeterHud hud, EventType type) {
		super(hud);

		this.setType(type);
	}

	@Override
	protected void drawEdges(GuiRenderer renderer, int x, int y, Meter meter, MeterEvent event) {
		if (vertical(event)) {
			int width = hud.settings.scale * (3 + hud.settings.wparity);
			int widthOffset = (hud.settings.columnWidth - width) / 2;
			int height = hud.settings.scale * (hud.settings.rowHeight / hud.settings.scale / 3 + 2);
			int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;

			if (hud.settings.columnWidth < width) {
				return;
			}

			int x0 = x + widthOffset;
			int y0 = y + heightOffset;
			int x1 = x0 + width;
			int y1 = y0 + height;
			int color = hud.settings.colorBackground;

			renderer.fill(x0, y0, x1, y1, color);
		} else {
			super.drawEdges(renderer, x, y, meter, event);
		}
	}

	@Override
	protected void drawCenter(GuiRenderer renderer, int x, int y, Meter meter, MeterEvent event) {
		if (vertical(event)) {
			int width = hud.settings.scale * (1 + hud.settings.wparity);
			int widthOffset = (hud.settings.columnWidth - width) / 2;
			int height = hud.settings.scale * (hud.settings.rowHeight / hud.settings.scale / 3);
			int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;

			if (hud.settings.columnWidth < width) {
				return;
			}

			int x0 = x + widthOffset;
			int y0 = y + heightOffset;
			int x1 = x0 + width;
			int y1 = y0 + height;
			int color = meter.getColor();

			renderer.fill(x0, y0, x1, y1, color);
		} else {
			super.drawCenter(renderer, x, y, meter, event);
		}
	}

	protected abstract boolean vertical(MeterEvent event);

}
