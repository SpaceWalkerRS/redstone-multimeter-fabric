package rsmm.fabric.client.gui.hud.event;

import rsmm.fabric.client.gui.hud.MultimeterHud;
import rsmm.fabric.common.meter.Meter;
import rsmm.fabric.common.meter.event.EventType;

public class PoweredEventRenderer extends ToggleEventRenderer {
	
	public PoweredEventRenderer(MultimeterHud hud) {
		super(hud, EventType.POWERED);
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
