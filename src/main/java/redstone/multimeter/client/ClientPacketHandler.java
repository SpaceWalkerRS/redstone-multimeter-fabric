package redstone.multimeter.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.common.network.PacketHandler;

public class ClientPacketHandler extends PacketHandler {

	private final MultimeterClient client;

	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}

	@Override
	protected Packet<?> toCustomPayload(ResourceLocation channel, FriendlyByteBuf data) {
		return new ServerboundCustomPayloadPacket(channel, data);
	}

	public void handlePacket(FriendlyByteBuf data) {
		try {
			decode(data).handle(client);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data.release();
		}
	}
}
