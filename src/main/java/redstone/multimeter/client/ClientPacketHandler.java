package redstone.multimeter.client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.resource.Identifier;

import redstone.multimeter.common.network.PacketHandler;

public class ClientPacketHandler extends PacketHandler {

	private final MultimeterClient client;

	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}

	@Override
	protected Packet<?> toCustomPayload(Identifier channel, PacketByteBuf data) {
		return new CustomPayloadC2SPacket(channel, data);
	}

	public void handlePacket(PacketByteBuf data) {
		try {
			decode(data).handle(client);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data.release();
		}
	}
}
