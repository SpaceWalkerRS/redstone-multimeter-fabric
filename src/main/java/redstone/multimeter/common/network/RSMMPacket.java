package redstone.multimeter.common.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.server.MultimeterServer;

public interface RSMMPacket {

	public void encode(NbtCompound data);

	public void decode(NbtCompound data);

	public void handle(MultimeterServer server, ServerPlayerEntity player);

	public void handle(MultimeterClient client);

}
