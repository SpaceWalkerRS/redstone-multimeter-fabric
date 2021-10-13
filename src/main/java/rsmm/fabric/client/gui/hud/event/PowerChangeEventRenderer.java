package rsmm.fabric.client.gui.hud.event;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.gui.hud.MultimeterHud;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;

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
		
		int half = hud.settings.rowHeight / 2;
		int color = centerColorProvider.apply(meter, event);
		
		if (increased) {
			drawRect(hud, matrices, x + 1, y + half, x + hud.settings.columnWidth, y + hud.settings.rowHeight - half, color);
		} else {
			drawRect(hud, matrices, x, y + half, x + hud.settings.columnWidth - 1, y + hud.settings.rowHeight - half, color);
		}
	}
}
