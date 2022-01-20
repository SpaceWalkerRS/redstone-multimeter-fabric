package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class TickPhaseTreePacket implements RSMMPacket {
	
	private NbtCompound nbt;
	
	public TickPhaseTreePacket() {
		
	}
	
	public TickPhaseTreePacket(NbtCompound nbt) {
		this.nbt = nbt;
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.put("tick phase tree", nbt);
	}
	
	@Override
	public void decode(NbtCompound data) {
		nbt = data.getCompound("tick phase tree");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.refreshTickPhaseTree(player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.refreshTickPhaseTree(nbt);
	}
}
