package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class ServerTickPacket implements RSMMPacket {
	
	private long currentServerTick;
	
	public ServerTickPacket() {
		
	}
	
	public ServerTickPacket(long serverTick) {
		currentServerTick = serverTick;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putLong("serverTime", currentServerTick);
	}
	
	@Override
	public void decode(NbtCompound data) {
		currentServerTick = data.getLong("serverTime");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.onServerTick(currentServerTick);
	}
}
