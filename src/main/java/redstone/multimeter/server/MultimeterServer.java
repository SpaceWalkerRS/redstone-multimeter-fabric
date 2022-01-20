package redstone.multimeter.server;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.TickPhaseTree;
import redstone.multimeter.common.TickTask;
import redstone.multimeter.common.network.packets.HandshakePacket;
import redstone.multimeter.common.network.packets.ServerTickPacket;
import redstone.multimeter.common.network.packets.TickPhaseTreePacket;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	private final Map<UUID, String> connectedPlayers;
	private final Map<UUID, String> playerNameCache;
	private final TickPhaseTree tickPhaseTree;
	
	private Field carpetTickSpeedProccessEntities;
	private TickPhase tickPhase;
	/** true if the OverWorld already ticked time */
	private boolean tickedTime;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter(this);
		this.connectedPlayers = new HashMap<>();
		this.playerNameCache = new HashMap<>();
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
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public TickPhaseTree getTickPhaseTree() {
		return tickPhaseTree;
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
		long tick = server.getWorld(DimensionType.OVERWORLD).getTime();
		
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
			
			if (server.getTicks() % 72000 == 0) {
				cleanPlayerNameCache();
			}
			if (shouldBuildTickPhaseTree()) {
				tickPhaseTree.start();
			}
		}
		
		tickPhase = TickPhase.UNKNOWN;
		multimeter.tickStart(paused);
	}
	
	private void cleanPlayerNameCache() {
		playerNameCache.keySet().removeIf(playerUUID -> {
			for (ServerMeterGroup meterGroup : multimeter.getMeterGroups()) {
				if (meterGroup.hasMember(playerUUID)) {
					return false;
				}
			}
			
			return true;
		});
	}
	
	private boolean shouldBuildTickPhaseTree() {
		return !tickPhaseTree.isComplete() && !tickPhaseTree.isBuilding();
	}
	
	public void tickEnd() {
		boolean paused = isPaused();
		
		if (!paused) {
			ServerTickPacket packet = new ServerTickPacket(getCurrentTick());
			
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (multimeter.hasSubscription(player)) {
					packetHandler.sendToPlayer(packet, player);
				}
			}
		}
		if (tickPhaseTree.isBuilding()) {
			tickPhaseTree.end();
		}
		
		tickPhase = TickPhase.UNKNOWN;
		multimeter.tickEnd(paused);
	}
	
	public void onPlayerJoin(ServerPlayerEntity player) {
		multimeter.onPlayerJoin(player);
		playerNameCache.remove(player.getUuid());
	}
	
	public void onPlayerLeave(ServerPlayerEntity player) {
		multimeter.onPlayerLeave(player);
		connectedPlayers.remove(player.getUuid());
		playerNameCache.put(player.getUuid(), player.getEntityName());
	}
	
	public void onHandshake(ServerPlayerEntity player, String modVersion) {
		if (connectedPlayers.put(player.getUuid(), modVersion) == null) {
			HandshakePacket packet = new HandshakePacket();
			packetHandler.sendToPlayer(packet, player);
			
			refreshTickPhaseTree(player);
		}
	}
	
	public void refreshTickPhaseTree(ServerPlayerEntity player) {
		if (tickPhaseTree.isComplete()) {
			TickPhaseTreePacket packet = new TickPhaseTreePacket(tickPhaseTree.toNbt());
			packetHandler.sendToPlayer(packet, player);
		}
	}
	
	public ServerWorld getWorld(Identifier dimensionId) {
		DimensionType type = DimensionType.byId(dimensionId);
		return server.getWorld(type);
	}
	
	public ServerWorld getWorldOf(DimPos pos) {
		return getWorld(pos.getDimensionId());
	}
	
	public BlockState getBlockState(DimPos pos) {
		World world = getWorldOf(pos);
		
		if (world != null) {
			return world.getBlockState(pos.getBlockPos());
		}
		
		return null;
	}
	
	public PlayerManager getPlayerManager() {
		return server.getPlayerManager();
	}
	
	public ServerPlayerEntity getPlayer(UUID playerUUID) {
		return server.getPlayerManager().getPlayer(playerUUID);
	}
	
	public String getPlayerName(UUID playerUUID) {
		ServerPlayerEntity player = getPlayer(playerUUID);
		return player == null ? playerNameCache.get(playerUUID) : player.getEntityName();
	}
	
	public ServerPlayerEntity getPlayer(String playerName) {
		return server.getPlayerManager().getPlayer(playerName);
	}
	
	public boolean isMultimeterClient(UUID playerUUID) {
		return connectedPlayers.containsKey(playerUUID);
	}
	
	public boolean isMultimeterClient(ServerPlayerEntity player) {
		return connectedPlayers.containsKey(player.getUuid());
	}
	
	public Collection<ServerPlayerEntity> collectPlayers(Collection<UUID> playerUUIDs) {
		Set<ServerPlayerEntity> players = new LinkedHashSet<>();
		
		for (UUID playerUUID : playerUUIDs) {
			ServerPlayerEntity player = getPlayer(playerUUID);
			
			if (player != null) {
				players.add(player);
			}
		}
		
		return players;
	}
	
	public void sendMessage(ServerPlayerEntity player, Text message, boolean actionBar) {
		player.addChatMessage(message, actionBar);
	}
}
