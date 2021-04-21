package rsmm.fabric.common.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.server.MultimeterServer;

public abstract class AbstractRSMMPacket {
	
	public abstract void encode(PacketByteBuf buffer);
	
	public abstract void decode(PacketByteBuf buffer);
	
	public abstract void execute(MultimeterServer server, ServerPlayerEntity player);
	
	public abstract void execute(MultimeterClient client);
	
}
