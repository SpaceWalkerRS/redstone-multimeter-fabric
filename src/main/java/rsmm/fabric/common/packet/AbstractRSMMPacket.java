package rsmm.fabric.common.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.server.MultimeterServer;

public abstract class AbstractRSMMPacket {
	
	public abstract void encode(CompoundTag data);
	
	public abstract void decode(CompoundTag data);
	
	public abstract void execute(MultimeterServer server, ServerPlayerEntity player);
	
	public abstract void execute(MultimeterClient client);
	
}
