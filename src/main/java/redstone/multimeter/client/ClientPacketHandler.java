package redstone.multimeter.client;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import redstone.multimeter.common.network.AbstractPacketHandler;
import redstone.multimeter.common.network.RSMMPacket;

public class ClientPacketHandler extends AbstractPacketHandler {
	
	private final MultimeterClient client;
	
	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}
	
	@Override
	protected Packet<?> toCustomPayload(Identifier id, PacketByteBuf buffer) {
		return new CustomPayloadC2SPacket(id.toString(), buffer);
	}
	
	@Override
	public <P extends RSMMPacket> void send(P packet) {
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
}
