package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class RemoveMeterPacket extends AbstractRSMMPacket {
	
private int meterIndex;
	
	public RemoveMeterPacket() {
		
	}
	
	public RemoveMeterPacket(int meterIndex) {
		this.meterIndex = meterIndex;
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.putInt("meterIndex", meterIndex);
	}
	
	@Override
	public void decode(CompoundTag data) {
		meterIndex = data.getInt("meterIndex");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.getMeterGroup().removeMeter(meterIndex);
	}
}
