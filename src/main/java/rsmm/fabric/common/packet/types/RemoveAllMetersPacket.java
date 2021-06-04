package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class RemoveAllMetersPacket extends AbstractRSMMPacket {
	
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
