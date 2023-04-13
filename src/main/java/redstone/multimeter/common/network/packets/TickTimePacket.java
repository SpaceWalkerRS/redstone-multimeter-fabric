package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

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
	public void encode(CompoundTag data) {
		data.putLong("game time", gameTime);
	}

	@Override
	public void decode(CompoundTag data) {
		gameTime = data.getLong("game time");
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
	}

	@Override
	public void handle(MultimeterClient client) {
		client.tickTime(gameTime);
	}
}
