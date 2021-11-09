package redstone.multimeter.client.gui.hud.event;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;

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
