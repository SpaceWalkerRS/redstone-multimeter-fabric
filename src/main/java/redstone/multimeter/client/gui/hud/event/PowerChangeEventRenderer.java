package redstone.multimeter.client.gui.hud.event;

import net.minecraft.client.util.math.MatrixStack;

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
	protected void drawCenter(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int metaData = event.getMetaData();
		int oldPower = (metaData >> 8) & 0xFF;
		int newPower = metaData        & 0xFF;
		
		boolean increased = (newPower > oldPower);
		
		int width = hud.settings.columnWidth - 1;
		int half = hud.settings.rowHeight / 2;
		int height = (2 * half < hud.settings.rowHeight) ? 1 : 2;
		int color = centerColorProvider.apply(meter, event);
		
		if (increased) {
			x += 1;
		}
		
		hud.renderer.drawRect(matrices, x, y + half, width, height, color);
	}
}
