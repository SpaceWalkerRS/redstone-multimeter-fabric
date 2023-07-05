package redstone.multimeter.common.network;

import io.netty.buffer.Unpooled;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.resource.Identifier;

public abstract class PacketHandler {

	public Packet<?> encode(RSMMPacket packet) {
		Identifier key = Packets.getKey(packet);

		if (key == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}

		NbtCompound data = new NbtCompound();
		packet.encode(data);

		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());

		buffer.writeIdentifier(key);
		buffer.writeNbtCompound(data);

		return toCustomPayload(Packets.getChannel(), buffer);
	}

	protected abstract Packet<?> toCustomPayload(Identifier channel, PacketByteBuf data);

	protected RSMMPacket decode(PacketByteBuf buffer) {
		Identifier key = buffer.readIdentifier();
		RSMMPacket packet = Packets.create(key);

		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + key);
		}

		NbtCompound data = buffer.readNbtCompound();
		packet.decode(data);

		return packet;
	}
}
