package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class RemoveMetersPacket extends AbstractRSMMPacket {
	
	public RemoveMetersPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.removeAllMeters(player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
