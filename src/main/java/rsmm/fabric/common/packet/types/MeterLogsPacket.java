package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeterLogsPacket extends AbstractRSMMPacket {
	
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
