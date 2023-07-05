package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class TickTimePacket implements RSMMPacket {

	private long gameTime;

	public TickTimePacket() {
	}

	public TickTimePacket(long serverTime) {
		this.gameTime = serverTime;
	}

	@Override
	public void encode(NbtCompound data) {
		data.putLong("game time", gameTime);
	}

	@Override
	public void decode(NbtCompound data) {
		gameTime = data.getLong("game time");
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayerEntity player) {
	}

	@Override
	public void handle(MultimeterClient client) {
		client.tickTime(gameTime);
	}
}
