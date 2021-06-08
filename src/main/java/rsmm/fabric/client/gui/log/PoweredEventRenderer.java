package rsmm.fabric.client.gui.log;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public class PoweredEventRenderer extends ToggleEventRenderer {
	
	public PoweredEventRenderer(MultimeterClient client) {
		super(client, EventType.POWERED);
	}
	
	@Override
	protected void updateMode(Meter meter) {
		mode = meter.isMetering(EventType.ACTIVE) ? Mode.TOP : Mode.ALL;
	}
	
	@Override
	protected boolean isToggled(Meter meter) {
		return meter.isPowered();
	}
}
