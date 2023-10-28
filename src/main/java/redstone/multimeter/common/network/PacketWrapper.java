package redstone.multimeter.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class PacketWrapper implements CustomPacketPayload {

	public RSMMPacket packet;

	public PacketWrapper() {
	}

	public PacketWrapper(RSMMPacket packet) {
		this.packet = packet;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		Packets.WRITER.accept(packet, buffer);
	}

	@Override
	public ResourceLocation id() {
		return Packets.getChannel();
	}
}
