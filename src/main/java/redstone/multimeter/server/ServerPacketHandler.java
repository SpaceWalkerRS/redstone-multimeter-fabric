package redstone.multimeter.server;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import redstone.multimeter.common.network.PacketHandler;

public class ServerPacketHandler extends PacketHandler {
	
	private final MultimeterServer server;
	
	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}

	@Override
	protected Packet<?> toCustomPayload(Identifier id, PacketByteBuf buffer) {
		return new CustomPayloadS2CPacket(id, buffer);
	}
	
	public void onPacketReceived(PacketByteBuf buffer, ServerPlayerEntity player) {
		try {
			decode(buffer).execute(server, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
