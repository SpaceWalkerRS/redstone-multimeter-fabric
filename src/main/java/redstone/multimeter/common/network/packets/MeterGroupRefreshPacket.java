package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

public class MeterGroupRefreshPacket implements RSMMPacket {

	private String name;
	private CompoundTag meterGroupData;

	public MeterGroupRefreshPacket() {
	}

	public MeterGroupRefreshPacket(MeterGroup meterGroup) {
		this.name = meterGroup.getName();
		this.meterGroupData = meterGroup.toNbt();
	}

	@Override
	public void encode(CompoundTag data) {
		data.putString("name", name);
		data.put("data", meterGroupData);
	}

	@Override
	public void decode(CompoundTag data) {
		name = data.getString("name").get();
		meterGroupData = data.getCompound("data").get();
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		Multimeter multimeter = server.getMultimeter();

		if (multimeter.hasSubscription(player)) {
			multimeter.refreshMeterGroup(player);
		}
	}

	@Override
	public void handle(MultimeterClient client) {
		ClientMeterGroup meterGroup = client.getMeterGroup();

		if (meterGroup.getName().equals(name)) {
			meterGroup.refresh(meterGroupData);
		} else {
			meterGroup.subscribe(-1, name);
		}
	}
}
