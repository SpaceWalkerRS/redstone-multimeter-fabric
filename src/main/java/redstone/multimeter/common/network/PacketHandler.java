package redstone.multimeter.common.network;

import io.netty.buffer.Unpooled;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public abstract class PacketHandler {

	public Packet<?> encode(RSMMPacket packet) {
		String key = Packets.getKey(packet);

		if (key == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}

		NbtCompound data = new NbtCompound();
		packet.encode(data);

		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());

		buffer.writeString(key);
		buffer.writeNbtCompound(data);

		return toCustomPayload(Packets.getChannel(), buffer);
	}

	protected abstract Packet<?> toCustomPayload(String channel, PacketByteBuf data);

	protected RSMMPacket decode(PacketByteBuf buffer) {
		String key = buffer.readString(32767);
		RSMMPacket packet = Packets.create(key);

		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + key);
		}

		NbtCompound data = buffer.readNbtCompound();
		packet.decode(data);

		return packet;
	}
}
