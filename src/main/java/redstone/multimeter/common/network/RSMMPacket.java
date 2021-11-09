package redstone.multimeter.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.server.MultimeterServer;

public interface RSMMPacket {
	
	public void encode(CompoundTag data);
	
	public void decode(CompoundTag data);
	
	public void execute(MultimeterServer server, ServerPlayerEntity player);
	
	public void execute(MultimeterClient client);
	
}
