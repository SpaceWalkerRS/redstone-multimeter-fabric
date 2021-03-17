package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class ToggleMeterPacket extends AbstractRSMMPacket {
	
	private WorldPos pos;
	
	public ToggleMeterPacket(WorldPos pos) {
		this.pos = pos;
	}
	
	public ToggleMeterPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		if (pos != null) {
			server.toggleMeter(pos, player);
		}
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
