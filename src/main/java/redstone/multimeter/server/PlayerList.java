package redstone.multimeter.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;

import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class PlayerList {

	private final MultimeterServer server;

	private final Map<UUID, ServerPlayer> playersByUuid;
	private final Map<String, ServerPlayer> playersByName;
	private final Map<UUID, String> nameCache;

	public PlayerList(MultimeterServer server) {
		this.server = server;

		this.playersByUuid = new HashMap<>();
		this.playersByName = new HashMap<>();
		this.nameCache = new HashMap<>();
	}

	public MultimeterServer getServer() {
		return server;
	}

	public void tick() {
		if (server.getTickCount() % 72000 == 0) {
			cleanNameCache();
		}
	}

	private void cleanNameCache() {
		Collection<ServerMeterGroup> meterGroups = server.getMultimeter().getMeterGroups();

		nameCache.keySet().removeIf(uuid -> {
			for (ServerMeterGroup meterGroup : meterGroups) {
				if (meterGroup.hasMember(uuid)) {
					return false;
				}
			}

			return true;
		});
	}

	public void add(ServerPlayer player) {
		if (!has(player.getUUID())) {
			playersByUuid.put(player.getUUID(), player);
			playersByName.put(player.getScoreboardName(), player);
			nameCache.remove(player.getUUID());

			server.onPlayerJoin(player);
		}
	}

	public void remove(ServerPlayer player) {
		if (has(player.getUUID())) {
			playersByUuid.remove(player.getUUID());
			playersByName.remove(player.getScoreboardName());
			nameCache.put(player.getUUID(), player.getScoreboardName());

			server.onPlayerLeave(player);
		}
	}

	public void respawn(ServerPlayer player) {
		if (has(player.getUUID())) {
			playersByUuid.put(player.getUUID(), player);
			playersByName.put(player.getScoreboardName(), player);
		}
	}

	public Collection<ServerPlayer> get() {
		return playersByUuid.values();
	}

	public ServerPlayer get(UUID uuid) {
		return playersByUuid.get(uuid);
	}

	public ServerPlayer get(String name) {
		return playersByName.get(name);
	}

	public boolean has(UUID uuid) {
		return playersByUuid.containsKey(uuid);
	}

	public boolean has(String name) {
		return playersByName.containsKey(name);
	}

	public String getName(UUID uuid) {
		ServerPlayer player = get(uuid);
		return player == null ? nameCache.get(uuid) : player.getScoreboardName();
	}

	public void send(RSMMPacket packet) {
		send(packet, player -> true);
	}

	public void send(RSMMPacket packet, ServerMeterGroup meterGroup) {
		send(packet, player -> meterGroup.hasSubscriber(player));
	}

	public void send(RSMMPacket packet, DimensionType dimension) {
		send(packet, player -> player.level.dimension.getType() == dimension);
	}

	public void send(RSMMPacket packet, Predicate<ServerPlayer> predicate) {
		Packet<?> mcPacket = server.getPacketHandler().encode(packet);

		for (ServerPlayer player : playersByUuid.values()) {
			if (predicate.test(player)) {
				player.connection.send(mcPacket);
			}
		}
	}

	public void send(RSMMPacket packet, ServerPlayer player) {
		Packet<?> mcPacket = server.getPacketHandler().encode(packet);
		player.connection.send(mcPacket);
	}

	public void updatePermissions(ServerPlayer player) {
		server.getMinecraftServer().getPlayerList().sendPlayerPermissionLevel(player);
	}
}
