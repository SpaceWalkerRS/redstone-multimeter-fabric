package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeterLogsDataPacket extends AbstractRSMMPacket {
	
	private CompoundTag logsData;
	
	public MeterLogsDataPacket() {
		
	}
	
	public MeterLogsDataPacket(CompoundTag data) {
		this.logsData = data;
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.put("logs", logsData);
	}
	
	@Override
	public void decode(CompoundTag data) {
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
