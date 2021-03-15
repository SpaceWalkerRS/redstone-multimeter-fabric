package rsmm.fabric.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

import rsmm.fabric.common.packet.AbstractPacketHandler;
import rsmm.fabric.common.packet.AbstractRSMMPacket;

public class ClientPacketHandler extends AbstractPacketHandler {
	
	private final MultimeterClient client;
	
	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}
	
	@Override
	protected Packet<?> toCustomPayloadPacket(PacketByteBuf buffer) {
		return new CustomPayloadC2SPacket(PACKET_IDENTIFIER, buffer);
	}
	
	@Override
	public void sendPacket(AbstractRSMMPacket packet) {
		client.getMinecraftClient().getNetworkHandler().sendPacket(encodePacket(packet));
	}
	
	public void onPacketReceived(PacketByteBuf buffer) {
		try {
			decodePacket(buffer).execute(client);
		} catch (Exception e) {
			
		}
	}
}
