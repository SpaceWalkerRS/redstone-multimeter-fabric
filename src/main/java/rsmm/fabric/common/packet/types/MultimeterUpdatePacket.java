package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MultimeterUpdatePacket extends AbstractRSMMPacket {
	
	private long currentTick;
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeLong(currentTick);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		currentTick = buffer.readLong();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.tick(currentTick);
	}
}
