package rsmm.fabric.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.common.packet.AbstractPacketHandler;
import rsmm.fabric.common.packet.AbstractRSMMPacket;

public class ServerPacketHandler extends AbstractPacketHandler {
	
	private final MultimeterServer server;
	
	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}
	
	@Override
	protected Packet<?> toCustomPayloadPacket(PacketByteBuf buffer) {
		return new CustomPayloadS2CPacket(PACKET_IDENTIFIER, buffer);
	}
	
	@Override
	public void sendPacket(AbstractRSMMPacket packet) {
		server.getMinecraftServer().getPlayerManager().sendToAll(encodePacket(packet));
	}
	
	public void sendPacketToPlayer(AbstractRSMMPacket packet, ServerPlayerEntity player) {
		player.networkHandler.sendPacket(encodePacket(packet));
	}
	
	public void onPacketReceived(PacketByteBuf buffer, ServerPlayerEntity player) {
		try {
			decodePacket(buffer).execute(server, player);
		} catch (Exception e) {
			
		}
	}
}
