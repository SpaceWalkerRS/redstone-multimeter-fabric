package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class RemoveMeterPacket implements RSMMPacket {
	
	private long id;
	
	public RemoveMeterPacket() {
		
	}
	
	public RemoveMeterPacket(long id) {
		this.id = id;
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.putLong("id", id);
	}
	
	@Override
	public void decode(CompoundTag data) {
		id = data.getLong("id");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().removeMeter(player, id);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
