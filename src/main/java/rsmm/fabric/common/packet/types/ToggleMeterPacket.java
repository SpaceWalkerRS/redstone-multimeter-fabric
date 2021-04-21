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
	private boolean movable;
	
	public ToggleMeterPacket() {
		
	}
	
	public ToggleMeterPacket(WorldPos pos, boolean movable) {
		this.pos = pos;
		this.movable = movable;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
		buffer.writeBoolean(movable);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
		movable = buffer.readBoolean();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().toggleMeter(pos, movable, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
