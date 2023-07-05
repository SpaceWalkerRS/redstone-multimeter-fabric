package redstone.multimeter.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.common.network.PacketHandler;

public class ServerPacketHandler extends PacketHandler {

	private final MultimeterServer server;

	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}

	@Override
	protected Packet<?> toCustomPayload(Identifier channel, PacketByteBuf data) {
		return new CustomPayloadS2CPacket(channel, data);
	}

	public void handlePacket(PacketByteBuf data, ServerPlayerEntity player) {
		try {
			decode(data).handle(server, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
