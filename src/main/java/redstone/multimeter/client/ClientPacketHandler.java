package redstone.multimeter.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import redstone.multimeter.common.network.AbstractPacketHandler;
import redstone.multimeter.common.network.RSMMPacket;

public class ClientPacketHandler extends AbstractPacketHandler {
	
	private final MultimeterClient client;
	
	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}
	
	@Override
	protected Packet<?> toCustomPayloadPacket(Identifier id, PacketByteBuf buffer) {
		return new CustomPayloadC2SPacket(id, buffer);
	}
	
	@Override
	public <P extends RSMMPacket> void sendPacket(P packet) {
		client.getMinecraftClient().getNetworkHandler().sendPacket(encodePacket(packet));
	}
	
	public void onPacketReceived(PacketByteBuf buffer) {
		try {
			decodePacket(buffer).execute(client);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			buffer.release();
		}
	}
}
