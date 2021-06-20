package rsmm.fabric.client.gui.log;

import static rsmm.fabric.client.gui.HudSettings.*;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;

public class PowerChangeEventRenderer extends BasicEventRenderer {
	
	public PowerChangeEventRenderer(MultimeterClient client) {
		super(client);
		
		this.setType(EventType.POWER_CHANGE);
	}
	
	@Override
	protected void drawCenter(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int metaData = event.getMetaData();
		int oldPower = (metaData >> 8) & 0xFF;
		int newPower = metaData        & 0xFF;
		
		boolean increased = (newPower > oldPower);
		
		int half = ROW_HEIGHT / 2;
		int color = centerColorProvider.apply(meter, event);
		
		if (increased) {
			fill(matrices, x + 1, y + half, x + COLUMN_WIDTH, y + ROW_HEIGHT - half, color);
		} else {
			fill(matrices, x, y + half, x + COLUMN_WIDTH - 1, y + ROW_HEIGHT - half, color);
		}
	}
}
