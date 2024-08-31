package redstone.multimeter.common.network.packets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.util.NbtUtils;

public class SetMetersPacket implements RSMMPacket {

	private List<MeterProperties> meters;

	public SetMetersPacket() {
	}

	public SetMetersPacket(List<MeterProperties> meters) {
		this.meters = meters;
	}

	@Override
	public void encode(NbtCompound data) {
		NbtList list = new NbtList();

		for (int i = 0; i < meters.size(); i++) {
			MeterProperties meter = meters.get(i);
			NbtCompound meterNbt = meter.toNbt();

			list.add(meterNbt);
		}

		data.put("meters", list);
	}

	@Override
	public void decode(NbtCompound data) {
		meters = new ArrayList<>();
		NbtList list = data.getList("meters", NbtUtils.TYPE_COMPOUND);

		for (int i = 0; i < list.size(); i++) {
			NbtCompound meterNbt = list.getCompound(i);
			MeterProperties meter = MeterProperties.fromNbt(meterNbt);

			meters.add(meter);
		}
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().setMeters(player, meters);
	}

	@Override
	public void handle(MultimeterClient client) {
	}
}
