package rsmm.fabric.common.packet.types;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.DimPos;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class RemoveMeterPacket extends AbstractRSMMPacket {
	
	private DimPos pos;
	
	public RemoveMeterPacket() {
		
	}
	
	public RemoveMeterPacket(DimPos pos) {
		this.pos = pos;
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
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		Meter meter = client.getMeterGroup().getMeterAt(pos);
		
		if (meter != null) {
			client.getMeterGroup().removeMeter(meter);
		}
	}
}
