package redstone.multimeter.common.network.packets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class SetMetersPacket implements RSMMPacket {

	private List<MeterProperties> meters;

	public SetMetersPacket() {
	}

	public SetMetersPacket(List<MeterProperties> meters) {
		this.meters = meters;
	}

	@Override
	public void encode(CompoundTag data) {
		ListTag list = new ListTag();

		for (int i = 0; i < meters.size(); i++) {
			MeterProperties meter = meters.get(i);
			CompoundTag meterNbt = meter.toNbt();

			list.add(meterNbt);
		}

		data.put("meters", list);
	}

	@Override
	public void decode(CompoundTag data) {
		meters = new ArrayList<>();
		ListTag list = data.getList("meters").get();

		for (int i = 0; i < list.size(); i++) {
			CompoundTag meterNbt = list.getCompound(i).get();
			MeterProperties meter = MeterProperties.fromNbt(meterNbt);

			meters.add(meter);
		}
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.getMultimeter().setMeters(player, meters);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
