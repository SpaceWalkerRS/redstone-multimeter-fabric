package rsmm.fabric.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.meter.MeterProperties;
import rsmm.fabric.common.network.RSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeterUpdatePacket implements RSMMPacket {
	
	private long id;
	private MeterProperties properties;
	
	public MeterUpdatePacket() {
		
	}
	
	public MeterUpdatePacket(long id, MeterProperties properties) {
		this.id = id;
		this.properties = properties;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putLong("id", id);
		data.put("properties", properties.toNBT());
	}
	
	@Override
	public void decode(NbtCompound data) {
		id = data.getLong("id");
		properties = MeterProperties.fromNBT(data.getCompound("properties"));
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().updateMeter(player, id, properties);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
