package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class RemoveAllMetersPacket implements RSMMPacket {
	
	public RemoveAllMetersPacket() {
		
	}
	
	@Override
	public void encode(NbtCompound data) {
		
	}
	
	@Override
	public void decode(NbtCompound data) {
		
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().clear();
	}
}
