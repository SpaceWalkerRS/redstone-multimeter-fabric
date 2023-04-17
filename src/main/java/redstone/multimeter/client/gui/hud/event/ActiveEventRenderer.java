package redstone.multimeter.client.gui.hud.event;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;

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
