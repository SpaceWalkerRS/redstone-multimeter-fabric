package redstone.multimeter.server;

import java.io.File;
import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.TickPhaseTree;
import redstone.multimeter.common.TickTask;
import redstone.multimeter.common.WorldPos;
import redstone.multimeter.common.network.packets.HandshakePacket;
import redstone.multimeter.common.network.packets.ServerTickPacket;
import redstone.multimeter.common.network.packets.TickPhaseTreePacket;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final PlayerList playerList;
	private final Multimeter multimeter;
	private final TickPhaseTree tickPhaseTree;
	
	private Field carpetTickSpeedProccessEntities;
	private TickPhase tickPhase;
	/** true if the OverWorld already ticked time */
	private boolean tickedTime;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.playerList = new PlayerList(this);
		this.multimeter = new Multimeter(this);
		this.tickPhaseTree = new TickPhaseTree();
		
		this.tickPhase = TickPhase.UNKNOWN;
		this.tickedTime = false;
		
		this.detectCarpetTickSpeed();
	}
	
	public MinecraftServer getMinecraftServer() {
		return server;
	}
	
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	public PlayerList getPlayerList() {
		return playerList;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public TickPhaseTree getTickPhaseTree() {
		return tickPhaseTree;
	}
	
	public long getTicks() {
		return server.getTicks();
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
	
	public boolean isDedicated() {
		return server.isDedicated();
	}
	
	public File getConfigFolder() {
		return new File(server.getRunDirectory(), RedstoneMultimeterMod.CONFIG_PATH);
	}
	
	public TickPhase getTickPhase() {
		return tickPhase;
	}
	
	public void startTickTask(boolean updateTree, TickTask task, String... args) {
		tickPhase = tickPhase.startTask(task);
		if (updateTree) {
			tickPhaseTree.startTask(task, args);
		}
	}
	
	public void endTickTask(boolean updateTree) {
		tickPhase = tickPhase.endTask();
		if (updateTree) {
			tickPhaseTree.endTask();
		}
	}
	
	public void swapTickTask(boolean updateTree, TickTask task, String... args) {
		tickPhase = tickPhase.swapTask(task);
		if (updateTree) {
			tickPhaseTree.swapTask(task, args);
		}
	}
	
	public void onOverworldTickTime() {
		tickedTime = true;
	}
	
	public long getCurrentTick() {
		long tick = server.getOverworld().getTime();
		
		if (!tickedTime) {
			tick++;
		}
		
		return tick;
	}
	
	public boolean isPaused() {
		boolean frozen = false;
		
		if (carpetTickSpeedProccessEntities != null) {
			try {
				frozen = !carpetTickSpeedProccessEntities.getBoolean(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				
			}
		}
		
		return frozen || ((IMinecraftServer)server).isPausedRSMM();
	}
	
	public void tickStart() {
		boolean paused = isPaused();
		
		if (!paused) {
			tickedTime = false;
			
			if (shouldBuildTickPhaseTree()) {
				tickPhaseTree.start();
			}
			
			playerList.tick();
		}
		
		tickPhase = TickPhase.UNKNOWN;
		multimeter.tickStart(paused);
	}
	
	private boolean shouldBuildTickPhaseTree() {
		return !tickPhaseTree.isComplete() && !tickPhaseTree.isBuilding();
	}
	
	public void tickEnd() {
		boolean paused = isPaused();
		
		if (!paused) {
			ServerTickPacket packet = new ServerTickPacket(getCurrentTick());
			playerList.send(packet, player -> multimeter.hasSubscription(player));
		}
		if (tickPhaseTree.isBuilding()) {
			tickPhaseTree.end();
		}
		
		tickPhase = TickPhase.UNKNOWN;
		multimeter.tickEnd(paused);
	}
	
	public void onHandshake(ServerPlayerEntity player, String modVersion) {
		if (!playerList.has(player.getUuid())) {
			playerList.add(player);

			HandshakePacket packet = new HandshakePacket();
			playerList.send(packet, player);
		}
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		multimeter.onPlayerJoin(player);
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		multimeter.onPlayerLeave(player);
	}
	
	public void refreshTickPhaseTree(ServerPlayerEntity player) {
		if (tickPhaseTree.isComplete()) {
			TickPhaseTreePacket packet = new TickPhaseTreePacket(tickPhaseTree.toNbt());
			playerList.send(packet, player);
		}
	}
	
	public ServerWorld getWorld(Identifier worldId) {
		RegistryKey<World> key = RegistryKey.of(Registry.DIMENSION, worldId);
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
	
	public boolean isMultimeterClient(UUID playerUUID) {
		return playerList.has(playerUUID);
	}
	
	public boolean isMultimeterClient(ServerPlayerEntity player) {
		return playerList.has(player.getUuid());
	}
	
	public void sendMessage(ServerPlayerEntity player, Text message, boolean actionBar) {
		player.sendMessage(message, actionBar);
	}
}
