package rsmm.fabric.common.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.server.MultimeterServer;

public interface RSMMPacket {
	
	public void encode(NbtCompound data);
	
	public void decode(NbtCompound data);
	
	public void execute(MultimeterServer server, ServerPlayerEntity player);
	
	public void execute(MultimeterClient client);
	
}
