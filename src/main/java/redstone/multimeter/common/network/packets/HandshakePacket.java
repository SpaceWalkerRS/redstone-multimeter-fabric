package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class HandshakePacket implements RSMMPacket {

	private String modVersion;

	public HandshakePacket() {
		modVersion = RedstoneMultimeterMod.MOD_VERSION;
	}

	@Override
	public void encode(CompoundTag data) {
		data.putString("mod version", modVersion);
	}

	@Override
	public void decode(CompoundTag data) {
		modVersion = data.getString("mod version").get();
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.onHandshake(player, modVersion);
	}

	@Override
	public void handle(MultimeterClient client) {
		client.onHandshake(modVersion);
	}
}
