package rsmm.fabric.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.network.RSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeterChangesPacket implements RSMMPacket {
	
	private NbtCompound meterData;
	
	public MeterChangesPacket() {
		
	}
	
	public MeterChangesPacket(NbtCompound meterData) {
		this.meterData = meterData;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.put("meterData", meterData);
	}
	
	@Override
	public void decode(NbtCompound data) {
		meterData = data.getCompound("meterData");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().updateMeters(meterData);
	}
}
