package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class JoinMultimeterServerPacket extends AbstractRSMMPacket {
	
	private long currentServerTick;
	
	public JoinMultimeterServerPacket() {
		
	}
	
	public JoinMultimeterServerPacket(long serverTick) {
		currentServerTick = serverTick;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeLong(currentServerTick);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		currentServerTick = buffer.readLong();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.onConnect(currentServerTick);
	}
}
