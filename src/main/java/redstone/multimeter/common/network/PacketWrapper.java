package redstone.multimeter.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PacketWrapper(RSMMPacket packet) implements CustomPacketPayload {

	public static final StreamCodec<FriendlyByteBuf, PacketWrapper> STREAM_CODEC = CustomPacketPayload.codec(Packets::encode, Packets::decode);
	public static final Type<PacketWrapper> TYPE = CustomPacketPayload.createType("rsmm");

	public PacketWrapper(RSMMPacket packet) {
		this.packet = packet;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
