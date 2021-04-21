package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class MeterGroupDataPacket extends AbstractRSMMPacket {
	
	private String name;
	private PacketByteBuf data;
	
	public MeterGroupDataPacket() {
		
	}
	
	public MeterGroupDataPacket(String name, PacketByteBuf data) {
		this.name = name;
		this.data = data;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(name);
		PacketUtils.writeData(buffer, data);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		data = PacketUtils.readData(buffer);
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
