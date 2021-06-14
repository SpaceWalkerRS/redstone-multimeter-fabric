package rsmm.fabric.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.network.RSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeterGroupDataPacket implements RSMMPacket {
	
	private String name;
	private NbtCompound meterGroupData;
	
	public MeterGroupDataPacket() {
		
	}
	
	public MeterGroupDataPacket(MeterGroup meterGroup) {
		this.name = meterGroup.getName();
		this.meterGroupData = meterGroup.toNBT();
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putString("name", name);
		data.put("data", meterGroupData);
	}
	
	@Override
	public void decode(NbtCompound data) {
		name = data.getString("name");
		meterGroupData = data.getCompound("data");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().meterGroupDataReceived(name, meterGroupData, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.meterGroupDataReceived(name, meterGroupData);
	}
}
