package redstone.multimeter.client.gui.hud.event;

import net.minecraft.client.gui.GuiGraphics;

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
	protected void drawCenter(GuiGraphics graphics, int x, int y, Meter meter, MeterEvent event) {
		int width = hud.settings.columnWidth - hud.settings.scale;
		int height = hud.settings.scale * (1 + hud.settings.hparity);
		int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;
		int color = meter.getColor();

		if (increased(event)) {
			x += hud.settings.scale;
		}

		hud.renderer.renderRect(graphics, x, y + heightOffset, width, height, color);
	}

	private boolean increased(MeterEvent event) {
		int metaData = event.getMetadata();
		int oldPower = (metaData >> 8) & 0xFF;
		int newPower = metaData & 0xFF;

		return newPower > oldPower;
	}
}
