package rsmm.fabric.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.common.packet.types.JoinMultimeterServerPacket;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter(this);
	}
	
	public MinecraftServer getMinecraftServer() {
		return server;
	}
	
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public void tick() {
		multimeter.tick();
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		JoinMultimeterServerPacket packet = new JoinMultimeterServerPacket();
		packetHandler.sendPacketToPlayer(packet, player);
		
		multimeter.onPlayerJoin(player);
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		multimeter.onPlayerLeave(player);
	}
}
