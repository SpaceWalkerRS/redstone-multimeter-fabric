package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class RenameMeterPacket extends AbstractRSMMPacket {
	
	private int index;
	private String name;
	
	public RenameMeterPacket() {
		
	}
	
	public RenameMeterPacket(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(index);
		buffer.writeString(name);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		index = buffer.readInt();
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		Meter meter = client.getMeterGroup().getMeter(index);
		
		if (meter != null) {
			meter.setName(name);
		}
	}
}
