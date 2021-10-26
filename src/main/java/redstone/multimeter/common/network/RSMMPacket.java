package redstone.multimeter.common.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.server.MultimeterServer;

public interface RSMMPacket {
	
	public void encode(NbtCompound data);
	
	public void decode(NbtCompound data);
	
	public void execute(MultimeterServer server, ServerPlayerEntity player);
	
	public void execute(MultimeterClient client);
	
}
