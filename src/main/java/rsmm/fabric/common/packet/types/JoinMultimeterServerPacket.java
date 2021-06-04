package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.RedstoneMultimeterMod;
import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class JoinMultimeterServerPacket extends AbstractRSMMPacket {
	
	private String modVersion;
	private long currentServerTick;
	
	public JoinMultimeterServerPacket() {
		
	}
	
	public JoinMultimeterServerPacket(long serverTick) {
		modVersion = RedstoneMultimeterMod.MOD_VERSION;
		currentServerTick = serverTick;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putString("modVersion", modVersion);
		data.putLong("serverTime", currentServerTick);
	}
	
	@Override
	public void decode(NbtCompound data) {
		modVersion = data.getString("modVersion");
		currentServerTick = data.getLong("serverTime");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.onConnect(modVersion, currentServerTick);
	}
}
