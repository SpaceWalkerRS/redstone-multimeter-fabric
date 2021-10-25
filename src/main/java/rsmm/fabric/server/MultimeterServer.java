package rsmm.fabric.server;

import java.io.File;
import java.lang.reflect.Field;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import rsmm.fabric.RedstoneMultimeterMod;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.network.packets.JoinMultimeterServerPacket;
import rsmm.fabric.common.network.packets.ServerTickPacket;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	
	private Field carpetTickSpeedProccessEntities;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter(this);
		
		this.detectCarpetTickSpeed();
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
	
	/**
	 * Carpet Mod allows players to freeze the game.
	 * We need to detect when this is happening so
	 * RSMM can freeze accordingly.
	 */
	private void detectCarpetTickSpeed() {
		Class<?> clazzTickSpeed = null;
		
		try {
			clazzTickSpeed = Class.forName("carpet.helpers.TickSpeed");
		} catch (ClassNotFoundException e) {
			
		}
		
		if (clazzTickSpeed != null) {
			try {
				carpetTickSpeedProccessEntities = clazzTickSpeed.getField("process_entities");
			} catch (NoSuchFieldException | SecurityException e) {
				
			}
		}
	}
	
	public boolean isPaused() {
		boolean frozen = false;
		
		if (carpetTickSpeedProccessEntities != null) {
			try {
				frozen = !carpetTickSpeedProccessEntities.getBoolean(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				
			}
		}
		
		return frozen || ((IMinecraftServer)server).isPaused();
	}
	
	public void tickStart() {
		multimeter.tickStart(isPaused());
	}
	
	public void tickEnd() {
		boolean paused = isPaused();
		
		if (!paused) {
			ServerTickPacket packet = new ServerTickPacket(multimeter.getCurrentTick());
			
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (multimeter.hasSubscription(player)) {
					packetHandler.sendPacketToPlayer(packet, player);
				}
			}
		}
		
		multimeter.tickEnd(paused);
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		JoinMultimeterServerPacket packet = new JoinMultimeterServerPacket(multimeter.getCurrentTick());
		packetHandler.sendPacketToPlayer(packet, player);
		
		multimeter.onPlayerJoin(player);
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		multimeter.onPlayerLeave(player);
	}
	
	public ServerWorld getWorld(Identifier worldId) {
		RegistryKey<World> key = RegistryKey.of(Registry.WORLD_KEY, worldId);
		return server.getWorld(key);
	}
	
	public ServerWorld getWorldOf(WorldPos pos) {
		return getWorld(pos.getWorldId());
	}
	
	public BlockState getBlockState(WorldPos pos) {
		World world = getWorldOf(pos);
		
		if (world != null) {
			return world.getBlockState(pos.getBlockPos());
		}
		
		return null;
	}
	
	public File getWorldSaveDataFolder() {
		File worldSave = server.getSavePath(WorldSavePath.ROOT).toFile();
		return new File(worldSave, RedstoneMultimeterMod.NAMESPACE);
	}
}
