package redstone.multimeter.server.compat;

import carpet.helpers.TickSpeed;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.server.MultimeterServer;

public class CarpetCompat {

	private final MultimeterServer server;

	public CarpetCompat(MultimeterServer server) {
		this.server = server;
	}

	public boolean isFrozen() {
		if (RedstoneMultimeterMod.isCarpetPreset()) {
			return !TickSpeed.process_entities;

		}

		return false;
	}
}
