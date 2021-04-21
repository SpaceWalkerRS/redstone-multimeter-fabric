package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class AddMeterPacket extends AbstractRSMMPacket {
	
	private WorldPos pos;
	private String name;
	private int color;
	private boolean movable;
	private int meteredEvents;
	private boolean powered;
	private boolean active;
	
	public AddMeterPacket() {
		
	}
	
	public AddMeterPacket(WorldPos pos, String name, int color, boolean movable, int meteredEvents, boolean powered, boolean active) {
		this.pos = pos;
		this.name = name;
		this.color = color;
		this.movable = movable;
		this.meteredEvents = meteredEvents;
		this.powered = powered;
		this.active = active;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
		buffer.writeString(name);
		buffer.writeInt(color);
		buffer.writeBoolean(movable);
		buffer.writeInt(meteredEvents);
		buffer.writeBoolean(powered);
		buffer.writeBoolean(active);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		color = buffer.readInt();
		movable = buffer.readBoolean();
		meteredEvents = buffer.readInt();
		powered = buffer.readBoolean();
		active = buffer.readBoolean();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		Meter meter = new Meter(pos, name, color, movable, meteredEvents, powered, active);
		client.getMeterGroup().addMeter(meter);
	}
}
