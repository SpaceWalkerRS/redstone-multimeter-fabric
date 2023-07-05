package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

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
	public void encode(NbtCompound data) {
		data.putString("mod version", modVersion);
	}

	@Override
	public void decode(NbtCompound data) {
		modVersion = data.getString("mod version");
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayerEntity player) {
		server.onHandshake(player, modVersion);
	}

	@Override
	public void handle(MultimeterClient client) {
		client.onHandshake(modVersion);
	}
}
