package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class JoinMultimeterServerPacket implements RSMMPacket {
	
	private String modVersion;
	private long currentServerTick;
	
	public JoinMultimeterServerPacket() {
		
	}
	
	public JoinMultimeterServerPacket(long serverTick) {
		modVersion = RedstoneMultimeterMod.MOD_VERSION;
		currentServerTick = serverTick;
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.putString("mod version", modVersion);
		data.putLong("server time", currentServerTick);
	}
	
	@Override
	public void decode(CompoundTag data) {
		modVersion = data.getString("mod version");
		currentServerTick = data.getLong("server time");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.onConnect(modVersion, currentServerTick);
	}
}
