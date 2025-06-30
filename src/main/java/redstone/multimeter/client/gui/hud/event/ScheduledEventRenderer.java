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
			if (hud.settings.columnWidth < (3 + hud.settings.wparity) * hud.settings.scale) {
				return;
			}

			int width = hud.settings.scale * (3 + hud.settings.wparity);
			int widthOffset = (hud.settings.columnWidth - width) / 2;
			int height = hud.settings.scale * (hud.settings.rowHeight / hud.settings.scale / 3 + 2);
			int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;
			int color = hud.settings.colorBackground;

			hud.renderer.renderRect(graphics, x + widthOffset, y + heightOffset, width, height, color);
		} else {
			super.drawEdges(graphics, x, y, meter, event);
		}
	}

	@Override
	protected void drawCenter(GuiGraphics graphics, int x, int y, Meter meter, MeterEvent event) {
		if (scheduling(event)) {
			int width = hud.settings.scale * (1 + hud.settings.wparity);
			int widthOffset = (hud.settings.columnWidth - width) / 2;
			int height = hud.settings.scale * (hud.settings.rowHeight / hud.settings.scale / 3);
			int heightOffset = (hud.settings.rowHeight - height) / 2 + hud.settings.hparity;
			int color = meter.getColor();

			hud.renderer.renderRect(graphics, x + widthOffset, y + heightOffset, width, height, color);
		} else {
			super.drawCenter(graphics, x, y, meter, event);
		}
	}

	protected boolean scheduling(MeterEvent event) {
		return (event.getMetadata() >> 30) == 1;
	}
}
