package redstone.multimeter.common.network;

import io.netty.buffer.Unpooled;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

public abstract class PacketHandler {
	
	public Packet<?> encode(RSMMPacket packet) {
		Identifier id = PacketManager.getId(packet);
		
		if (id == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}
		
		NbtCompound data = new NbtCompound();
		packet.encode(data);
		
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		
		buffer.writeIdentifier(id);
		buffer.writeNbt(data);
		
		return toCustomPayload(PacketManager.getChannelId(), buffer);
	}
	
	protected abstract Packet<?> toCustomPayload(Identifier id, PacketByteBuf buffer);
	
	protected RSMMPacket decode(PacketByteBuf buffer) {
		Identifier id = buffer.readIdentifier();
		RSMMPacket packet = PacketManager.create(id);
		
		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + id);
		}
		
		NbtCompound data = buffer.readNbt();
		packet.decode(data);
		
		return packet;
	}
}
