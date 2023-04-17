package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class RebuildTickPhaseTreePacket implements RSMMPacket {

	public RebuildTickPhaseTreePacket() {
	}

	@Override
	public void encode(CompoundTag data) {
	}

	@Override
	public void decode(CompoundTag data) {
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.rebuildTickPhaseTree(player);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
