package rsmm.fabric.client.gui.hud.event;

import rsmm.fabric.client.gui.hud.MultimeterHud;
import rsmm.fabric.common.meter.Meter;
import rsmm.fabric.common.meter.event.EventType;

public class ActiveEventRenderer extends ToggleEventRenderer {
	
	public ActiveEventRenderer(MultimeterHud hud) {
		super(hud, EventType.ACTIVE);
	}
	
	@Override
	protected void updateMode(Meter meter) {
		mode = meter.isMetering(EventType.POWERED) ? Mode.BOTTOM : Mode.ALL;
	}
	
	@Override
	protected boolean isToggled(Meter meter) {
		return meter.isActive();
	}
}
