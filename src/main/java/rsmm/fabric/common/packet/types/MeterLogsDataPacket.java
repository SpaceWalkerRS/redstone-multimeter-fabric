package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeterLogsDataPacket extends AbstractRSMMPacket {
	
	private CompoundTag data;
	
	public MeterLogsDataPacket() {
		
	}
	
	public MeterLogsDataPacket(CompoundTag data) {
		this.data = data;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeCompoundTag(data);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		data = buffer.readCompoundTag();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().getLogManager().updateMeterLogs(data);
	}
}
