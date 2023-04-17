package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class AddMeterPacket implements RSMMPacket {

	private MeterProperties properties;

	public AddMeterPacket() {
	}

	public AddMeterPacket(MeterProperties properties) {
		this.properties = properties;
	}

	@Override
	public void encode(CompoundTag data) {
		data.put("properties", properties.toNbt());
	}

	@Override
	public void decode(CompoundTag data) {
		properties = MeterProperties.fromNbt(data.getCompound("properties"));
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.getMultimeter().addMeter(player, properties);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
