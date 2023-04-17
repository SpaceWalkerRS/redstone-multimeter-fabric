package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;
import redstone.multimeter.util.NbtUtils;

public class MeterLogsPacket implements RSMMPacket {

	private ListTag logsData;

	public MeterLogsPacket() {
	}

	public MeterLogsPacket(ListTag logsData) {
		this.logsData = logsData;
	}

	@Override
	public void encode(CompoundTag data) {
		data.put("logs", logsData);
	}

	@Override
	public void decode(CompoundTag data) {
		logsData = data.getList("logs", NbtUtils.TYPE_COMPOUND);
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
	}

	@Override
	public void handle(MultimeterClient client) {
		client.getMeterGroup().getLogManager().updateMeterLogs(logsData);
	}
}
