package rsmm.fabric.client.gui.log;

import static rsmm.fabric.client.gui.HudSettings.*;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;

public class MovedEventRenderer extends BasicEventRenderer {
	
	public MovedEventRenderer() {
		super(EventType.MOVED);
	}
	
	@Override
	protected void drawEvent(MatrixStack matrices, int x, int y, Meter meter, MeterEvent event) {
		int half = ROW_HEIGHT / 2;
		
		fill(matrices, x, y + half - 1, x + COLUMN_WIDTH, y + ROW_HEIGHT - half + 1, meter.getColor());
		fill(matrices, x, y + half, x + COLUMN_WIDTH, y + ROW_HEIGHT - half, 0xFFFFFFFF);
	}
}
