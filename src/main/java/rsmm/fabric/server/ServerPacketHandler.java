package rsmm.fabric.server;

import java.util.Collection;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import rsmm.fabric.common.network.AbstractPacketHandler;
import rsmm.fabric.common.network.RSMMPacket;

public class ServerPacketHandler extends AbstractPacketHandler {
	
	private final MultimeterServer server;
	
	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}

	@Override
	protected Packet<?> toCustomPayloadPacket(Identifier id, PacketByteBuf buffer) {
		return new CustomPayloadS2CPacket(id, buffer);
	}
	
	@Override
	public <P extends RSMMPacket> void sendPacket(P packet) {
		Packet<?> mcPacket = encodePacket(packet);
		server.getMinecraftServer().getPlayerManager().sendToAll(mcPacket);
	}
	
	public <P extends RSMMPacket> void sendPacketToPlayer(P packet, ServerPlayerEntity player) {
		player.networkHandler.sendPacket(encodePacket(packet));
	}
	
	public <P extends RSMMPacket> void sendPacketToPlayers(P packet, Collection<ServerPlayerEntity> players) {
		Packet<?> mcPacket = encodePacket(packet);
		
		for (ServerPlayerEntity player : players) {
			player.networkHandler.sendPacket(mcPacket);
		}
	}
	
	public void onPacketReceived(PacketByteBuf buffer, ServerPlayerEntity player) {
		try {
			decodePacket(buffer).execute(server, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
