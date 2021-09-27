package rsmm.fabric.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.network.RSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class AddMeterPacket implements RSMMPacket {
	
	private MeterProperties properties;
	
	public AddMeterPacket() {
		
	}
	
	public AddMeterPacket(MeterProperties properties) {
		this.properties = properties;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.put("settings", properties.toNBT());
	}
	
	@Override
	public void decode(NbtCompound data) {
		properties = MeterProperties.fromNBT(data.getCompound("settings"));
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().addMeter(player, properties);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
