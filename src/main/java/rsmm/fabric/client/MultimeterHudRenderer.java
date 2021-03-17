package rsmm.fabric.client;

import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.log.MeterGroupLogs;

public class MultimeterHudRenderer {
	
	private final MultimeterClient client;
	
	public MultimeterHudRenderer(MultimeterClient client) {
		this.client = client;
	}
	
	public void render(MatrixStack matrices) {
		MeterGroup meterGroup = client.getMeterGroup();
		MeterGroupLogs logs = meterGroup.getLogs();
	}
}
