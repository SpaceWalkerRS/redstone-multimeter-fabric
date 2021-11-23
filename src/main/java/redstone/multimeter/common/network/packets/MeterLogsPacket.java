package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterLogsPacket implements RSMMPacket {
	
	private CompoundTag logsData;
	
	public MeterLogsPacket() {
		
	}
	
	public MeterLogsPacket(CompoundTag data) {
		this.logsData = data;
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.method_10566("logs", logsData);
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
