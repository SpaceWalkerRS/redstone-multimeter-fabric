package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class TeleportToMeterPacket implements RSMMPacket {
	
	private long id;
	
	public TeleportToMeterPacket() {
		
	}
	
	public TeleportToMeterPacket(long id) {
		this.id = id;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putLong("id", id);
	}
	
	@Override
	public void decode(NbtCompound data) {
		id = data.getLong("id");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().teleportToMeter(player, id);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
