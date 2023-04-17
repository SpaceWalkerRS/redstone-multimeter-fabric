package redstone.multimeter.common.network;

import io.netty.buffer.Unpooled;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public abstract class PacketHandler {

	public Packet<?> encode(RSMMPacket packet) {
		ResourceLocation key = Packets.getKey(packet);

		if (key == null) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}

		CompoundTag data = new CompoundTag();
		packet.encode(data);

		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

		buffer.writeResourceLocation(key);
		buffer.writeNbt(data);

		return toCustomPayload(Packets.getChannel(), buffer);
	}

	protected abstract Packet<?> toCustomPayload(ResourceLocation channel, FriendlyByteBuf data);

	protected RSMMPacket decode(FriendlyByteBuf buffer) {
		ResourceLocation key = buffer.readResourceLocation();
		RSMMPacket packet = Packets.create(key);

		if (packet == null) {
			throw new IllegalStateException("Unable to decode packet: " + key);
		}

		CompoundTag data = buffer.readNbt();
		packet.decode(data);

		return packet;
	}
}
