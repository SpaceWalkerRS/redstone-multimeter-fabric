package rsmm.fabric.common.packet.types;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class RemoveMeterPacket extends AbstractRSMMPacket {
	
	private int meterIndex;
	
	public RemoveMeterPacket() {
		
	}
	
	public RemoveMeterPacket(int pos) {
		this.meterIndex = pos;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(meterIndex);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		meterIndex = buffer.readInt();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().removeMeter(meterIndex);
	}
}
