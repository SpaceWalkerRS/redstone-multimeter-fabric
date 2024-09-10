package redstone.multimeter.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class PlayerList {

	private final MultimeterServer server;

	private final Map<UUID, ServerPlayerEntity> playersByUuid;
	private final Map<String, ServerPlayerEntity> playersByName;
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
		if (server.getTicks() % 72000 == 0) {
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

	public void add(ServerPlayerEntity player) {
		if (!has(player.getUuid())) {
			playersByUuid.put(player.getUuid(), player);
			playersByName.put(player.getName(), player);
			nameCache.remove(player.getUuid());

			server.onPlayerJoin(player);
		}
	}

	public void remove(ServerPlayerEntity player) {
		if (has(player.getUuid())) {
			playersByUuid.remove(player.getUuid());
			playersByName.remove(player.getName());
			nameCache.put(player.getUuid(), player.getName());

			server.onPlayerLeave(player);
		}
	}

	public void respawn(ServerPlayerEntity player) {
		if (has(player.getUuid())) {
			playersByUuid.put(player.getUuid(), player);
			playersByName.put(player.getName(), player);
		}
	}

	public Collection<ServerPlayerEntity> get() {
		return playersByUuid.values();
	}

	public ServerPlayerEntity get(UUID uuid) {
		return playersByUuid.get(uuid);
	}

	public ServerPlayerEntity get(String name) {
		return playersByName.get(name);
	}

	public boolean has(UUID uuid) {
		return playersByUuid.containsKey(uuid);
	}

	public boolean has(String name) {
		return playersByName.containsKey(name);
	}

	public String getName(UUID uuid) {
		ServerPlayerEntity player = get(uuid);
		return player == null ? nameCache.get(uuid) : player.getName();
	}

	public void send(RSMMPacket packet) {
		send(packet, player -> true);
	}

	public void send(RSMMPacket packet, ServerMeterGroup meterGroup) {
		send(packet, player -> meterGroup.hasSubscriber(player));
	}

	public void send(RSMMPacket packet, int dimension) {
		send(packet, player -> player.world.dimension.id == dimension);
	}

	public void send(RSMMPacket packet, Predicate<ServerPlayerEntity> predicate) {
		Packet mcPacket = server.getPacketHandler().encode(packet);

		for (ServerPlayerEntity player : playersByUuid.values()) {
			if (predicate.test(player)) {
				player.networkHandler.sendPacket(mcPacket);
			}
		}
	}

	public void send(RSMMPacket packet, ServerPlayerEntity player) {
		Packet mcPacket = server.getPacketHandler().encode(packet);
		player.networkHandler.sendPacket(mcPacket);
	}
}
