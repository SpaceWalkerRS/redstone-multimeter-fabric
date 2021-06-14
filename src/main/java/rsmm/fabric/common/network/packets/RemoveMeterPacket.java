package rsmm.fabric.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.network.RSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class RemoveMeterPacket implements RSMMPacket {
	
	private int meterIndex;
	
	public RemoveMeterPacket() {
		
	}
	
	public RemoveMeterPacket(int meterIndex) {
		this.meterIndex = meterIndex;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putInt("meterIndex", meterIndex);
	}
	
	@Override
	public void decode(NbtCompound data) {
		meterIndex = data.getInt("meterIndex");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().removeMeter(meterIndex, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().removeMeter(meterIndex);
	}
}
