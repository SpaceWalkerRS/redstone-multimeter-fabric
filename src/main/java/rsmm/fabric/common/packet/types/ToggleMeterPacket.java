package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class ToggleMeterPacket extends AbstractRSMMPacket {
	
	private CompoundTag properties;
	
	public ToggleMeterPacket() {
		
	}
	
	public ToggleMeterPacket(CompoundTag properties) {
		this.properties = properties;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeCompoundTag(properties);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		properties = buffer.readCompoundTag();
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().toggleMeter(properties, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
