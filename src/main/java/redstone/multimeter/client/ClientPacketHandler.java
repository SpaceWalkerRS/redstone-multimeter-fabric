package redstone.multimeter.client;

import redstone.multimeter.common.network.PacketWrapper;

public class ClientPacketHandler {

	private final MultimeterClient client;

	public ClientPacketHandler(MultimeterClient client) {
		this.client = client;
	}

	public void handlePacket(PacketWrapper wrapper) {
		try {
			wrapper.packet.handle(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
