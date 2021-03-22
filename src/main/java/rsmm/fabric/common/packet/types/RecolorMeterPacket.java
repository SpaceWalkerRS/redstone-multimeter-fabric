package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class RecolorMeterPacket extends AbstractRSMMPacket {
	
	private int index;
	private int color;
	
	public RecolorMeterPacket() {
		
	}
	
	public RecolorMeterPacket(int index, int color) {
		this.index = index;
		this.color = color;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(index);
		buffer.writeInt(color);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		index = buffer.readInt();
		color = buffer.readInt();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().recolorMeter(index, color, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		Meter meter = client.getMeterGroup().getMeter(index);
		
		if (meter != null) {
			meter.setColor(color);
		}
	}
}
