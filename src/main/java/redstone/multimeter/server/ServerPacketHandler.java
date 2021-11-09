package redstone.multimeter.server;

import java.util.Collection;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import redstone.multimeter.common.network.AbstractPacketHandler;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class ServerPacketHandler extends AbstractPacketHandler {
	
	private final MultimeterServer server;
	
	public ServerPacketHandler(MultimeterServer server) {
		this.server = server;
	}

	@Override
	protected Packet<?> toCustomPayload(Identifier id, PacketByteBuf buffer) {
		return new CustomPayloadS2CPacket(id, buffer);
	}
	
	@Override
	public <P extends RSMMPacket> void send(P packet) {
		Packet<?> mcPacket = encode(packet);
		server.getMinecraftServer().getPlayerManager().sendToAll(mcPacket);
	}
	
	public <P extends RSMMPacket> void sendToPlayer(P packet, ServerPlayerEntity player) {
		player.networkHandler.sendPacket(encode(packet));
	}
	
	public <P extends RSMMPacket> void sendToPlayers(P packet, Collection<ServerPlayerEntity> players) {
		Packet<?> mcPacket = encode(packet);
		
		for (ServerPlayerEntity player : players) {
			player.networkHandler.sendPacket(mcPacket);
		}
	}
	
	public <P extends RSMMPacket> void sendToSubscribers(P packet, ServerMeterGroup meterGroup) {
		sendToPlayers(packet, server.collectPlayers(meterGroup.getSubscribers()));
	}
	
	public void onPacketReceived(PacketByteBuf buffer, ServerPlayerEntity player) {
		try {
			decode(buffer).execute(server, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
