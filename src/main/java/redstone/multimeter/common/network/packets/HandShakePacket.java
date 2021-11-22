package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class HandShakePacket implements RSMMPacket {
	
	private String modVersion;
	
	public HandShakePacket() {
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
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.onHandshake(player, modVersion);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.onHandshake(modVersion);
	}
}
