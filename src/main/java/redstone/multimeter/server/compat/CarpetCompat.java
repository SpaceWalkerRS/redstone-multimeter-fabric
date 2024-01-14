package redstone.multimeter.server.compat;

import carpet.fakes.MinecraftServerInterface;
import carpet.helpers.ServerTickRateManager;

import net.minecraft.server.MinecraftServer;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.server.MultimeterServer;

public class CarpetCompat {

	private final MultimeterServer server;

	public CarpetCompat(MultimeterServer server) {
		this.server = server;
	}

	public boolean isFrozen() {
		if (RedstoneMultimeterMod.isCarpetPreset()) {
			MinecraftServer server = this.server.getMinecraftServer();
			ServerTickRateManager trm = ((MinecraftServerInterface)server).getTickRateManager();

			return !trm.runsNormally();

		}

		return false;
	}
}
