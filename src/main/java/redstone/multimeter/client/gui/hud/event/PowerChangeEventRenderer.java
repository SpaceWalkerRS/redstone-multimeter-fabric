package redstone.multimeter.client.gui.hud.event;

import redstone.multimeter.client.gui.GuiRenderer;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;

public class PowerChangeEventRenderer extends BasicEventRenderer {

	public PowerChangeEventRenderer(MultimeterHud hud) {
		super(hud);

		this.setType(EventType.POWER_CHANGE);
	}

	@Override
	protected void drawCenter(GuiRenderer renderer, int x, int y, Meter meter, MeterEvent event) {
		int width = hud.settings.columnWidth - hud.settings.scale;
		int height = hud.settings.scale * (1 + hud.settings.hparity);
		int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;

		int x0 = x;
		int y0 = y + heightOffset;
		int x1 = x0 + width;
		int y1 = y0 + height;
		int color = meter.getColor();

		if (increased(event)) {
			x0 += hud.settings.scale;
			x1 += hud.settings.scale;
		}

		renderer.fill(x0, y0, x1, y1, color);
	}

	private boolean increased(MeterEvent event) {
		int metaData = event.getMetadata();
		int oldPower = (metaData >> 8) & 0xFF;
		int newPower = metaData & 0xFF;

		return newPower > oldPower;
	}
}
