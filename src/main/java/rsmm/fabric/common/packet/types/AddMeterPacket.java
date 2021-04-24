package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class AddMeterPacket extends AbstractRSMMPacket {
	
	private CompoundTag tag;
	
	public AddMeterPacket() {
		
	}
	
	public AddMeterPacket(Meter meter) {
		this.tag = meter.toTag();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeCompoundTag(tag);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		tag = buffer.readCompoundTag();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		Meter meter = Meter.createFromTag(tag);
		client.getMeterGroup().addMeter(meter);
	}
}
