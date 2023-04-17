package redstone.multimeter.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.server.MultimeterServer;

public interface RSMMPacket {

	public void encode(CompoundTag data);

	public void decode(CompoundTag data);

	public void handle(MultimeterServer server, ServerPlayer player);

	public void handle(MultimeterClient client);

}
