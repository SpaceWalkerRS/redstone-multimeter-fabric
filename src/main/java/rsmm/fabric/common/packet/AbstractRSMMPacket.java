package rsmm.fabric.common.packet;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.server.MultimeterServer;

public abstract class AbstractRSMMPacket {
	
	public abstract void encode(NbtCompound data);
	
	public abstract void decode(NbtCompound data);
	
	public abstract void execute(MultimeterServer server, ServerPlayerEntity player);
	
	public abstract void execute(MultimeterClient client);
	
}
