package redstone.multimeter.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.common.network.PacketHandler;

public class ServerPacketHandler extends PacketHandler {

	private final MultimeterServer server;

	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}

	@Override
	protected Packet<?> toCustomPayload(ResourceLocation channel, FriendlyByteBuf data) {
		return new ClientboundCustomPayloadPacket(channel, data);
	}

	public void handlePacket(FriendlyByteBuf data, ServerPlayer player) {
		try {
			decode(data).handle(server, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
