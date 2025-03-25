package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class TickPhaseTreePacket implements RSMMPacket {

	private CompoundTag nbt;

	public TickPhaseTreePacket() {
	}

	public TickPhaseTreePacket(CompoundTag nbt) {
		this.nbt = nbt;
	}

	@Override
	public void encode(CompoundTag data) {
		data.put("tick phase tree", nbt);
	}

	@Override
	public void decode(CompoundTag data) {
		nbt = data.getCompound("tick phase tree").get();
	}

	@Override
	public void handle(MultimeterServer server, ServerPlayer player) {
		server.refreshTickPhaseTree(player);
	}

	@Override
	public void handle(MultimeterClient client) {
		client.refreshTickPhaseTree(nbt);
	}
}
