package redstone.multimeter.common.network;

import java.io.IOException;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.PacketByteBuf;

import redstone.multimeter.util.Identifier;

public abstract class AbstractPacketHandler {
	
	protected <P extends RSMMPacket> Packet encode(P packet) {
		Identifier id = PacketManager.getId(packet);
		
		if (id == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}
		
		CompoundTag data = new CompoundTag();
		packet.encode(data);
		
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		
		buffer.method_7423(id.toString());
		buffer.writeCompoundTag(data);
		
		return toCustomPayload(PacketManager.getPacketChannelId(), buffer);
	}
	
	protected abstract Packet toCustomPayload(Identifier id, PacketByteBuf buffer);
	
	public abstract <P extends RSMMPacket> void send(P packet);
	
	protected <P extends RSMMPacket> P decode(PacketByteBuf buffer) throws IOException {
		Identifier id = new Identifier(buffer.readString(32767));
		P packet = PacketManager.createPacket(id);
		
		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + id);
		}
		
		CompoundTag data = buffer.readCompoundTag();
		packet.decode(data);
		
		return packet;
	}
}
