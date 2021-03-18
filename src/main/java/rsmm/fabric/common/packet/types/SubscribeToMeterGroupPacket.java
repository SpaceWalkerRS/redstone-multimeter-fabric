package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class SubscribeToMeterGroupPacket extends AbstractRSMMPacket {
	
	private String name;
	
	public SubscribeToMeterGroupPacket() {
		
	}
	
	public SubscribeToMeterGroupPacket(String name) {
		this.name = name;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(name);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.subscribeToMeterGroup(name, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
