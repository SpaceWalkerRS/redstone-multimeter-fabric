package rsmm.fabric.common.packet.types;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class MeterLogsDataPacket extends AbstractRSMMPacket {
	
	private PacketByteBuf data;
	
	public MeterLogsDataPacket() {
		
	}
	
	public MeterLogsDataPacket(PacketByteBuf data) {
		this.data = data;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeData(buffer, data);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		data = PacketUtils.readData(buffer);
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().getLogManager().updateMeterLogs(data);
	}
}
