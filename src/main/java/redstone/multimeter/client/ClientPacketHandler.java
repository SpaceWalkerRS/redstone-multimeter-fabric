package redstone.multimeter.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import redstone.multimeter.common.network.AbstractPacketHandler;
import redstone.multimeter.common.network.RSMMPacket;

public class ClientPacketHandler extends AbstractPacketHandler {
	
	private final MultimeterClient client;
	
	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}
	
	@Override
	protected Packet<?> toCustomPayload(String id, PacketBuffer buffer) {
		return new CPacketCustomPayload(id, buffer);
	}
	
	@Override
	public <P extends RSMMPacket> void send(P packet) {
		client.getMinecraftClient().getConnection().sendPacket(encode(packet));
	}
	
	public void onPacketReceived(PacketBuffer buffer) {
		try {
			decode(buffer).execute(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
