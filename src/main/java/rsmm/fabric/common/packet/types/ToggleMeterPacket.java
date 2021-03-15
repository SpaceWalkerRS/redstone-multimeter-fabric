package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class ToggleMeterPacket extends AbstractRSMMPacket {
	
	private BlockPos pos;
	
	public ToggleMeterPacket(BlockPos pos) {
		this.pos = pos;
	}
	
	public ToggleMeterPacket() {
		
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeBlockPos(pos);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		if (pos != null) {
			server.toggleMeter(pos, player);
		}
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.toggleMeter();
	}
}
