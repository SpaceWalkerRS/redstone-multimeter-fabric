package redstone.multimeter.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class AbstractPacketHandler {
	
	protected <P extends RSMMPacket> Packet<?> encode(P packet) {
		Identifier id = PacketManager.getId(packet);
		
		if (id == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}
		
		NbtCompound data = new NbtCompound();
		packet.encode(data);
		
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		
		buffer.writeIdentifier(id);
		buffer.writeNbt(data);
		
		return toCustomPayload(PacketManager.getPacketChannelId(), buffer);
	}
	
	protected abstract Packet<?> toCustomPayload(Identifier id, PacketByteBuf buffer);
	
	public abstract <P extends RSMMPacket> void send(P packet);
	
	protected <P extends RSMMPacket> P decode(PacketByteBuf buffer) {
		Identifier id = buffer.readIdentifier();
		P packet = PacketManager.createPacket(id);
		
		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + id);
		}
		
		NbtCompound data = buffer.readNbt();
		packet.decode(data);
		
		return packet;
	}
}
