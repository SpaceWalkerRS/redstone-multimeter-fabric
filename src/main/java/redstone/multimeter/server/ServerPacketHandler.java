package redstone.multimeter.server;

import net.minecraft.server.level.ServerPlayer;

import redstone.multimeter.common.network.PacketWrapper;

public class ServerPacketHandler  {

	private final MultimeterServer server;

	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}

	public void handlePacket(PacketWrapper wrapper, ServerPlayer player) {
		try {
			wrapper.packet().handle(server, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
