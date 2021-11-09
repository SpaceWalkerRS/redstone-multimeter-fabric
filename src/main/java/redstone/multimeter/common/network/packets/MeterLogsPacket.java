package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterLogsPacket implements RSMMPacket {
	
	private NbtCompound logsData;
	
	public MeterLogsPacket() {
		
	}
	
	public MeterLogsPacket(NbtCompound data) {
		this.logsData = data;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.put("logs", logsData);
	}
	
	@Override
	public void decode(NbtCompound data) {
		logsData = data.getCompound("logs");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().getLogManager().updateMeterLogs(logsData);
	}
}
