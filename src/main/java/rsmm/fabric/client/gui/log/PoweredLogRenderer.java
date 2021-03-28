package rsmm.fabric.client.gui.log;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public class PoweredLogRenderer extends LogRenderer {

	protected PoweredLogRenderer() {
		super(EventType.POWERED);
	}

	@Override
	public void render(MatrixStack matrices, Meter meter, int x, int y) {
		
	}
}
