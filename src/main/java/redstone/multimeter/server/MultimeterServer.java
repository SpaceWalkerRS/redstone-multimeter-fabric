package redstone.multimeter.server;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.TickTask;
import redstone.multimeter.common.network.packets.HandshakePacket;
import redstone.multimeter.common.network.packets.ServerTickPacket;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.meter.ServerMeterGroup;
import redstone.multimeter.util.DimensionUtils;

public class MultimeterServer {
	
	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final Multimeter multimeter;
	private final Map<UUID, String> connectedPlayers;
	private final Map<UUID, String> playerNameCache;
	
	private TickPhase tickPhase;
	/** true if the OverWorld already ticked time */
	private boolean tickedTime;
	
	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.multimeter = new Multimeter(this);
		this.connectedPlayers = new HashMap<>();
		this.playerNameCache = new HashMap<>();
		
		this.tickPhase = TickPhase.UNKNOWN;
		this.tickedTime = false;
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
	
	public boolean isDedicated() {
		return server.isDedicated();
	}
	
	public File getConfigFolder() {
		return new File(server.getRunDirectory(), RedstoneMultimeterMod.CONFIG_PATH);
	}
	
	public TickPhase getTickPhase() {
		return tickPhase;
	}
	
	public void startTickTask(TickTask task) {
		tickPhase = tickPhase.startTask(task);
	}
	
	public void endTickTask() {
		tickPhase = tickPhase.endTask();
	}
	
	public void swapTickTask(TickTask task) {
		tickPhase = tickPhase.swapTask(task);
	}
	
	public void onOverworldTickTime() {
		tickedTime = true;
	}
	
	public long getCurrentTick() {
		long tick = server.getWorld().getLastUpdateTime();
		
		if (!tickedTime) {
			tick++;
		}
		
		return tick;
	}
	
	public boolean isPaused() {
		return ((IMinecraftServer)server).isPaused();
	}
	
	public void tickStart() {
		boolean paused = isPaused();
		
		if (!paused) {
			tickedTime = false;
			
			if (server.getTicks() % 72000 == 0) {
				cleanPlayerNameCache();
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
	
	public void tickEnd() {
		boolean paused = isPaused();
		
		if (!paused) {
			ServerTickPacket packet = new ServerTickPacket(getCurrentTick());
			
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayers()) {
				if (multimeter.hasSubscription(player)) {
					packetHandler.sendToPlayer(packet, player);
				}
			}
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
		playerNameCache.put(player.getUuid(), player.getTranslationKey());
	}
	
	public void onHandshake(ServerPlayerEntity player, String modVersion) {
		if (connectedPlayers.put(player.getUuid(), modVersion) == null) {
			HandshakePacket packet = new HandshakePacket();
			packetHandler.sendToPlayer(packet, player);
		}
	}
	
	public ServerWorld getWorld(Identifier dimensionId) {
		Integer type = DimensionUtils.getRawId(dimensionId);
		return type == null ? null : server.getWorld(type);
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
	
	public ServerPlayerEntity getPlayer(UUID playerUUID) {
		return server.getPlayerManager().getPlayer(playerUUID);
	}
	
	public String getPlayerName(UUID playerUUID) {
		ServerPlayerEntity player = getPlayer(playerUUID);
		return player == null ? playerNameCache.get(playerUUID) : player.getTranslationKey();
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
		player.sendMessage(message);
	}
}
