package rsmm.fabric.client;

import java.util.List;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;

public class MultimeterHudRenderer extends DrawableHelper {
	
	private final MultimeterClient client;
	
	private boolean paused;
	
	public MultimeterHudRenderer(MultimeterClient client) {
		this.client = client;
	}
	
	public void render(MatrixStack matrices) {
		MeterGroup meterGroup = client.getMeterGroup();
		List<Meter> meters = meterGroup.getMeters();
		
		if (meters.isEmpty()) {
			return;
		}
	}
	
	public void pause() {
		paused = !paused;
	}
	
	public void stepForward(int amount) {
		
	}
	
	public void stepBackward(int amount) {
		
	}
}
