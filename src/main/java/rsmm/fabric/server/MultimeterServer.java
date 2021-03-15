package rsmm.fabric.server;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.RedstoneMultimeter;
import rsmm.fabric.common.logs.TickEventLogs;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Map<ServerWorld, Multimeter> multimeters;
	private final TickEventLogs logs;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeters = new HashMap<>();
		this.logs = new TickEventLogs();
	}
	
	public MinecraftServer getMinecraftServer() {
		return server;
	}
	
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public void registerMultimeter(Multimeter multimeter) {
		if (multimeters.putIfAbsent(multimeter.getWorld(), multimeter) != null) {
			RedstoneMultimeter.LOGGER.warn("Cannot register multiple multimeters for the same world!");
		}
	}
	
	public void toggleMeter(BlockPos pos, ServerPlayerEntity player) {
		Multimeter multimeter = multimeters.get(player.world);
		
		if (multimeter != null) {
			multimeter.toggleMeter(pos, player);
		}
	}
	
	public void syncClientLogs() {
		
	}
}
