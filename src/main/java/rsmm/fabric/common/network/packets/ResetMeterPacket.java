package rsmm.fabric.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.network.RSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class ResetMeterPacket implements RSMMPacket {
	
	private long id;
	private MeterProperties newProperties;
	
	public ResetMeterPacket() {
		
	}
	
	public ResetMeterPacket(long id, MeterProperties newProperties) {
		this.id = id;
		this.newProperties = newProperties;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putLong("id", id);
		data.put("properties", newProperties.toNBT());
	}
	
	@Override
	public void decode(NbtCompound data) {
		id = data.getLong("id");
		newProperties = MeterProperties.fromNBT(data.getCompound("properties"));
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().resetMeter(player, id, newProperties);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
