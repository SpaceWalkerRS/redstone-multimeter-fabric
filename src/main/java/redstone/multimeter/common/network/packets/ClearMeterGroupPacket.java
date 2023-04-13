package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class ClearMeterGroupPacket implements RSMMPacket {

	public ClearMeterGroupPacket() {
	}

	@Override
	public void encode(CompoundTag data) {
	}

	@Override
	public void decode(CompoundTag data) {
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
	}

	@Override
	public void handle(MultimeterClient client) {
		client.getMeterGroup().clear();
	}
}
