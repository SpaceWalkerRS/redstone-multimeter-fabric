package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class MeterGroupDataPacket extends AbstractRSMMPacket {
	
	private String name;
	private CompoundTag data;
	
	public MeterGroupDataPacket() {
		
	}
	
	public MeterGroupDataPacket(MeterGroup meterGroup) {
		this.name = meterGroup.getName();
		this.data = meterGroup.toTag();
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(name);
		buffer.writeCompoundTag(data);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		data = buffer.readCompoundTag();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().meterGroupDataReceived(name, data, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.meterGroupDataReceived(name, data);
	}
}
