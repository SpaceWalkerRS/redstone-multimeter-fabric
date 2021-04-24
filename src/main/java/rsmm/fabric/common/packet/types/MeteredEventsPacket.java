package rsmm.fabric.common.packet.types;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeteredEventsPacket extends AbstractRSMMPacket {
	
	private int index;
	private EventType type;
	private boolean start;
	
	public MeteredEventsPacket() {
		
	}
	
	public MeteredEventsPacket(int index, EventType type, boolean start) {
		this.index = index;
		this.type = type;
		this.start = start;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(index);
		buffer.writeByte(type.getIndex());
		buffer.writeBoolean(start);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		index = buffer.readInt();
		type = EventType.fromIndex(buffer.readByte());
		start = buffer.readBoolean();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		if (type != null) {
			Meter meter = client.getMeterGroup().getMeter(index);
			
			if (meter != null) {
				if (start) {
					meter.startMetering(type);
				} else {
					meter.stopMetering(type);
				}
			}
		}
	}
}
