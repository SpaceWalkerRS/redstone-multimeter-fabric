package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class TeleportToMeterPacket extends AbstractRSMMPacket {
	
	private int meterIndex;
	
	public TeleportToMeterPacket() {
		
	}
	
	public TeleportToMeterPacket(int meterIndex) {
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
		server.getMultimeter().teleportToMeter(meterIndex, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
