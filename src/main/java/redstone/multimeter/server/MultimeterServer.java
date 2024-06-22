package redstone.multimeter.server;

import java.nio.file.Path;
import java.util.UUID;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.TickPhaseTree;
import redstone.multimeter.common.TickTask;
import redstone.multimeter.common.network.packets.HandshakePacket;
import redstone.multimeter.common.network.packets.TickTimePacket;
import redstone.multimeter.common.network.packets.TickPhaseTreePacket;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;

public class MultimeterServer {

	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final PlayerList playerList;
	private final Multimeter multimeter;
	private final TickPhaseTree tickPhaseTree;

	private boolean loaded;
	private TickPhase tickPhase;

	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.playerList = new PlayerList(this);
		this.multimeter = new Multimeter(this);
		this.tickPhaseTree = new TickPhaseTree();

		this.tickPhase = TickPhase.UNKNOWN;
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

	public long getTickCount() {
		return server.getTickCount();
	}

	public boolean isDedicatedServer() {
		return server.isDedicatedServer();
	}

	public Path getConfigDirectory() {
		return server.getServerDirectory().toPath().resolve(RedstoneMultimeterMod.CONFIG_PATH);
	}

	public TickPhase getTickPhase() {
		return tickPhase;
	}

	public void levelLoaded() {
		loaded = true;
	}

	public void startTickTask(TickTask task, String... args) {
		tickPhase = tickPhase.startTask(task);
		if (tickPhaseTree.isBuilding()) {
			tickPhaseTree.startTask(task, args);
		}
	}

	public void endTickTask() {
		tickPhase = tickPhase.endTask();
		if (tickPhaseTree.isBuilding()) {
			tickPhaseTree.endTask();
		}
	}

	public void swapTickTask(TickTask task, String... args) {
		tickPhase = tickPhase.swapTask(task);
		if (tickPhaseTree.isBuilding()) {
			tickPhaseTree.swapTask(task, args);
		}
	}

	public boolean isPaused() {
		return ((IMinecraftServer)server).rsmm$isPaused();
	}

	public boolean isFrozen() {
		return !server.tickRateManager().runsNormally();
	}

	public boolean isPausedOrFrozen() {
		return isPaused() || isFrozen();
	}

	public void tickStart() {
		boolean paused = isPaused();

		if (!paused) {
			if (shouldBuildTickPhaseTree()) {
				tickPhaseTree.start();
			}

			playerList.tick();
		}

		tickPhase = TickPhase.UNKNOWN;
		multimeter.tickStart(paused);
	}

	private boolean shouldBuildTickPhaseTree() {
		return loaded && !tickPhaseTree.isComplete() && !tickPhaseTree.isBuilding() && !isPausedOrFrozen() && !playerList.get().isEmpty();
	}

	public void tickEnd() {
		boolean paused = isPaused();

		if (tickPhaseTree.isBuilding()) {
			tickPhaseTree.end();
		}

		tickPhase = TickPhase.UNKNOWN;
		multimeter.tickEnd(paused);
	}

	public void tickTime(Level level) {
		TickTimePacket packet = new TickTimePacket(level.getGameTime());
		playerList.send(packet, level.dimension());
	}

	public void onHandshake(ServerPlayer player, String modVersion) {
		if (!playerList.has(player.getUUID())) {
			playerList.add(player);

			HandshakePacket packet = new HandshakePacket();
			playerList.send(packet, player);
		}
	}

	public void onPlayerJoin(ServerPlayer player) {
		multimeter.onPlayerJoin(player);
	}

	public void onPlayerLeave(ServerPlayer player) {
		multimeter.onPlayerLeave(player);
	}

	public void refreshTickPhaseTree(ServerPlayer player) {
		if (tickPhaseTree.isComplete()) {
			TickPhaseTreePacket packet = new TickPhaseTreePacket(tickPhaseTree.toNbt());
			playerList.send(packet, player);
		}
	}

	public void rebuildTickPhaseTree(ServerPlayer player) {
		if (tickPhaseTree.isComplete()) {
			tickPhaseTree.reset();
		}
	}

	public Iterable<ServerLevel> getLevels() {
		return server.getAllLevels();
	}

	public ServerLevel getLevel(ResourceLocation key) {
		return server.getLevel(ResourceKey.create(Registries.DIMENSION, key));
	}

	public ServerLevel getLevel(DimPos pos) {
		return getLevel(pos.getDimension());
	}

	public BlockState getBlockState(DimPos pos) {
		Level level = getLevel(pos);

		if (level == null) {
			return null;
		}

		return level.getBlockState(pos.getBlockPos());
	}

	public PlayerList getPlayerList() {
		return playerList;
	}

	public boolean isMultimeterClient(UUID uuid) {
		return playerList.has(uuid);
	}

	public boolean isMultimeterClient(ServerPlayer player) {
		return playerList.has(player.getUUID());
	}

	public void sendMessage(ServerPlayer player, Component message, boolean actionBar) {
		player.sendSystemMessage(message, actionBar);
	}
}
