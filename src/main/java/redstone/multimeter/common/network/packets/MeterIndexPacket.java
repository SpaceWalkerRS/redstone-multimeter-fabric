package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterIndexPacket implements RSMMPacket {

	private long id;
	private int index;

	public MeterIndexPacket() {
	}

	public MeterIndexPacket(long id, int index) {
		this.id = id;
		this.index = index;
	}

	@Override
	public void encode(CompoundTag data) {
		data.putLong("id", id);
		data.putInt("index", index);
	}

	@Override
	public void decode(CompoundTag data) {
		id = data.getLong("id");
		index = data.getInt("index");
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.getMultimeter().setMeterIndex(player, id, index);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
