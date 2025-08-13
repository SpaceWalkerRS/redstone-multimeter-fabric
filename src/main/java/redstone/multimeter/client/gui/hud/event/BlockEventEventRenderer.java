package redstone.multimeter.client.gui.hud.event;

import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.BlockEventStatus;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;

public class BlockEventEventRenderer extends StatusEventRenderer {

	public BlockEventEventRenderer(MultimeterHud hud) {
		super(hud, EventType.BLOCK_EVENT);
	}

	@Override
	protected boolean vertical(MeterEvent event) {
		return BlockEventStatus.byId(event.getMetadata() >> 29) == BlockEventStatus.QUEUED;
	}
}
