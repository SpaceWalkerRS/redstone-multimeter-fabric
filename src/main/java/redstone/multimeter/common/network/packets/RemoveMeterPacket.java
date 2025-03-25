package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

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
		id = data.getLong("id").get();
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.getMultimeter().removeMeter(player, id);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
