package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class MeterLogsPacket implements RSMMPacket {

	private NbtList logsData;

	public MeterLogsPacket() {
	}

	public MeterLogsPacket(NbtList logsData) {
		this.logsData = logsData;
	}

	@Override
	public void encode(NbtCompound data) {
		data.put("logs", logsData);
	}

	@Override
	public void decode(NbtCompound data) {
		logsData = data.getList("logs");
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayerEntity player) {
	}

	@Override
	public void handle(MultimeterClient client) {
		client.getMeterGroup().getLogManager().updateMeterLogs(logsData);
	}
}
