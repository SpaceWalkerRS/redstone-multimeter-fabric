package redstone.multimeter.server;

import java.nio.file.Path;
import java.util.UUID;

import net.minecraft.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.TickPhaseTree;
import redstone.multimeter.common.TickTask;
import redstone.multimeter.common.network.packets.HandshakePacket;
import redstone.multimeter.common.network.packets.TickTimePacket;
import redstone.multimeter.common.network.packets.TickPhaseTreePacket;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.util.DimensionUtils;
//import redstone.multimeter.server.compat.CarpetCompat;

public class MultimeterServer {

	private final MinecraftServer server;
	private final ServerPacketHandler packetHandler;
	private final PlayerList playerList;
	private final Multimeter multimeter;
	private final TickPhaseTree tickPhaseTree;

//	private final CarpetCompat carpetCompat;

	private boolean loaded;
	private TickPhase tickPhase;

	public MultimeterServer(MinecraftServer server) {
		this.server = server;
		this.packetHandler = new ServerPacketHandler(this);
		this.playerList = new PlayerList(this);
		this.multimeter = new Multimeter(this);
		this.tickPhaseTree = new TickPhaseTree();

//		this.carpetCompat = new CarpetCompat();

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

	public long getTicks() {
		return server.getTicks();
	}

	public boolean isDedicated() {
		return server.isDedicated();
	}

	public Path getConfigDirectory() {
		return server.getRunDir().toPath().resolve(RedstoneMultimeterMod.CONFIG_PATH);
	}

	public TickPhase getTickPhase() {
		return tickPhase;
	}

	public void worldLoaded() {
		loaded = true;

//		carpetCompat.init();
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

	public TickTask getCurrentTickTask() {
		return tickPhase.peekTask();
	}

	public boolean isPaused() {
		return ((IMinecraftServer)server).rsmm$isPaused();
	}

	public boolean isPausedOrFrozen() {
		return isPaused()/* || carpetCompat.isFrozen()*/;
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

	public void tickTime(World world) {
		TickTimePacket packet = new TickTimePacket(world.getTime());
		playerList.send(packet, world.dimension.getType());
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

	public void rebuildTickPhaseTree(ServerPlayerEntity player) {
		if (tickPhaseTree.isComplete()) {
			tickPhaseTree.reset();
		}
	}

	public ServerWorld[] getWorlds() {
		return server.worlds;
	}

	public ServerWorld getWorld(String key) {
		DimensionType dimension = DimensionUtils.byKey(key);
		return dimension == null ? null : server.getWorld(dimension.getId());
	}

	public ServerWorld getWorld(DimPos pos) {
		return getWorld(pos.getDimension());
	}

	public BlockState getBlockState(DimPos pos) {
		World world = getWorld(pos);

		if (world == null) {
			return null;
		}

		return world.getBlockState(pos.getBlockPos());
	}

	public PlayerList getPlayerList() {
		return playerList;
	}

	public boolean isMultimeterClient(UUID uuid) {
		return playerList.has(uuid);
	}

	public boolean isMultimeterClient(ServerPlayerEntity player) {
		return playerList.has(player.getUuid());
	}

	public void sendMessage(ServerPlayerEntity player, Text message) {
		player.addMessage(message);
	}
}
