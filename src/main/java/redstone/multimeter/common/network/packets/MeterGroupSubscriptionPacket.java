package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MeterGroupSubscriptionPacket implements RSMMPacket {

	private String name;
	private boolean subscribe;

	public MeterGroupSubscriptionPacket() {
	}

	public MeterGroupSubscriptionPacket(String name, boolean subscribed) {
		this.name = name;
		this.subscribe = subscribed;
	}

	@Override
	public void encode(CompoundTag data) {
		data.putString("name", name);
		data.putBoolean("subscribe", subscribe);
	}

	@Override
	public void decode(CompoundTag data) {
		name = data.getString("name");
		subscribe = data.getBoolean("subscribe");
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		Multimeter multimeter = server.getMultimeter();
		ServerMeterGroup meterGroup = multimeter.getMeterGroup(name);

		if (subscribe) {
			if (meterGroup == null) {
				multimeter.createMeterGroup(player, name);
			} else {
				multimeter.subscribeToMeterGroup(meterGroup, player);
			}
		} else {
			if (meterGroup == null) {
				multimeter.refreshMeterGroup(player);
			} else {
				multimeter.unsubscribeFromMeterGroup(meterGroup, player);
			}
		}
	}

	@Override
	public void handle(MultimeterClient client) {
		ClientMeterGroup meterGroup = client.getMeterGroup();
		ClientMeterGroup meterGroupPreview = client.getMeterGroupPreview();

		if (subscribe) {
			client.handleSubscribeToMeterGroup(name);
		} else {
			meterGroup.unsubscribe(false);
		}

		meterGroupPreview.stopPreviewing();

		if (client.getHud().isFocusMode()) {
			client.getHud().setFocusMode(false);
		}
	}
}
