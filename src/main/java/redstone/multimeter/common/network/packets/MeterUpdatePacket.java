package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterUpdatePacket implements RSMMPacket {

	private long id;
	private MeterProperties properties;

	public MeterUpdatePacket() {
	}

	public MeterUpdatePacket(long id, MeterProperties properties) {
		this.id = id;
		this.properties = properties;
	}

	@Override
	public void encode(CompoundTag data) {
		data.putLong("id", id);
		data.put("properties", properties.toNbt());
	}

	@Override
	public void decode(CompoundTag data) {
		id = data.getLong("id");
		properties = MeterProperties.fromNbt(data.getCompound("properties"));
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.getMultimeter().updateMeter(player, id, properties);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
