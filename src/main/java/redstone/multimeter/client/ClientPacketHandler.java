package redstone.multimeter.client;

import java.io.DataInputStream;

import net.minecraft.network.packet.CustomPayloadPacket;
import net.minecraft.network.packet.Packet;

import redstone.multimeter.common.network.PacketHandler;

public class ClientPacketHandler extends PacketHandler {

	private final MultimeterClient client;

	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}

	@Override
	protected Packet toCustomPayload(String channel, byte[] data) {
		return new CustomPayloadPacket(channel, data);
	}

	public void handlePacket(DataInputStream input) {
		try {
			decode(input).handle(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
