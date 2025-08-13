package redstone.multimeter.client.gui.hud.event;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;

public class ScheduledTickEventRenderer extends StatusEventRenderer {

	public ScheduledTickEventRenderer(MultimeterHud hud) {
		super(hud, EventType.SCHEDULED_TICK);
	}

	@Override
	protected boolean vertical(MeterEvent event) {
		return (event.getMetadata() >> 30) == 1;
	}
}
