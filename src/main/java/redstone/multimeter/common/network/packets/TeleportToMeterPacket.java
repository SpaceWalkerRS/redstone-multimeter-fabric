package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

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
	public void handle(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().teleportToMeter(player, id);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
