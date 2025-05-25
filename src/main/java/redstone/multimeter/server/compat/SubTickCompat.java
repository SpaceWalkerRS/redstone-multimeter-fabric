package redstone.multimeter.server.compat;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.server.MultimeterServer;

import subtick.ITickHandleable;

public class SubTickCompat {

	private final MultimeterServer server;

	public SubTickCompat(MultimeterServer server) {
		this.server = server;
	}

	public boolean isFrozen() {
		if (RedstoneMultimeterMod.isSubtickPresent()) {
			return ((ITickHandleable)server.getMinecraftServer()).tickHandler().frozen();
		}

		return false;
	}
}
