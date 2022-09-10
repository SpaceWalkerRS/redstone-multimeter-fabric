package redstone.multimeter.client;

//import net.earthcomputer.multiconnect.api.ICustomPayloadEvent;
//import net.earthcomputer.multiconnect.api.MultiConnectAPI;

//import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import redstone.multimeter.common.network.PacketHandler;
//import redstone.multimeter.common.network.PacketManager;
import redstone.multimeter.common.network.RSMMPacket;

public class ClientPacketHandler extends PacketHandler {
	
	private final MultimeterClient client;
	
	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
		
		/*MultiConnectAPI multiConnect = MultiConnectAPI.instance();
		
		multiConnect.addClientboundIdentifierCustomPayloadListener(event -> {
			if (PacketManager.getPacketChannelId().equals(event.getChannel())) {
				handleIncomingPayloadEvent(event);
			}
		});
		multiConnect.addClientboundStringCustomPayloadListener(event -> {
			if (PacketManager.getPacketChannelId().toString().equals(event.getChannel())) {
				handleIncomingPayloadEvent(event);
			}
		});
		multiConnect.addServerboundIdentifierCustomPayloadListener(event -> {
			if (PacketManager.getPacketChannelId().equals(event.getChannel())) {
				multiConnect.forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
			}
		});
		multiConnect.addServerboundStringCustomPayloadListener(event -> {
			if (PacketManager.getPacketChannelId().toString().equals(event.getChannel())) {
				multiConnect.forceSendStringCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
			}
		});*/
	}
	
	@Override
	protected Packet<?> toCustomPayload(Identifier id, PacketByteBuf buffer) {
		return new CustomPayloadC2SPacket(id, buffer);
	}

	public void send(RSMMPacket packet) {
		client.getMinecraftClient().getNetworkHandler().sendPacket(encode(packet));
	}
	
	public void onPacketReceived(PacketByteBuf buffer) {
		try {
			decode(buffer).execute(client);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			buffer.release();
		}
	}
	
	/*private void handleIncomingPayloadEvent(ICustomPayloadEvent<?> event) {
		MinecraftClient minecraftClient = client.getMinecraftClient();
		
		if (minecraftClient.isOnThread()) {
			onPacketReceived(event.getData());
		} else {
			minecraftClient.execute(() -> {
				if (event.getNetworkHandler().getConnection().isOpen()) {
					onPacketReceived(event.getData());
				}
			});
		}
	}*/
}
