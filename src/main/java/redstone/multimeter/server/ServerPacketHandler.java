package redstone.multimeter.server;

import java.io.DataInputStream;

import net.minecraft.network.packet.CustomPayloadPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.common.network.PacketHandler;

public class ServerPacketHandler extends PacketHandler {

	private final MultimeterServer server;

	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}

	@Override
	protected Packet toCustomPayload(String channel, byte[] data) {
		return new CustomPayloadPacket(channel, data);
	}

	public void handlePacket(DataInputStream input, ServerPlayerEntity player) {
		try {
			decode(input).handle(server, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
