package redstone.multimeter.client;

import net.earthcomputer.multiconnect.api.ICustomPayloadEvent;
import net.earthcomputer.multiconnect.api.MultiConnectAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.common.network.PacketHandler;
import redstone.multimeter.common.network.Packets;

public class ClientPacketHandler extends PacketHandler {

	private final MultimeterClient client;

	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;

		MultiConnectAPI multiConnect = MultiConnectAPI.instance();

		multiConnect.addClientboundIdentifierCustomPayloadListener(event -> {
			if (Packets.getChannel().equals(event.getChannel())) {
				handlePacket(event);
			}
		});
		multiConnect.addClientboundStringCustomPayloadListener(event -> {
			if (Packets.getChannel().toString().equals(event.getChannel())) {
				handlePacket(event);
			}
		});
		multiConnect.addServerboundIdentifierCustomPayloadListener(event -> {
			if (Packets.getChannel().equals(event.getChannel())) {
				multiConnect.forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
			}
		});
		multiConnect.addServerboundStringCustomPayloadListener(event -> {
			if (Packets.getChannel().toString().equals(event.getChannel())) {
				multiConnect.forceSendStringCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
			}
		});
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

	private void handlePacket(ICustomPayloadEvent<?> event) {
		Minecraft minecraft = client.getMinecraft();

		if (minecraft.isSameThread()) {
			handlePacket(event.getData());
		} else {
			minecraft.execute(() -> {
				if (event.getNetworkHandler().getConnection().isConnected()) {
					handlePacket(event.getData());
				}
			});
		}
	}
}
