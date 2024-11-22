package redstone.multimeter.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class PlayerList {

	private final MultimeterServer server;
	private final Map<String, ServerPlayerEntity> playersByName;

	public PlayerList(MultimeterServer server) {
		this.server = server;

		this.playersByName = new HashMap<>();
	}

	public MultimeterServer getServer() {
		return server;
	}

	public void add(ServerPlayerEntity player) {
		if (!has(player.getDisplayName())) {
			playersByName.put(player.getDisplayName(), player);

			server.onPlayerJoin(player);
		}
	}

	public void remove(ServerPlayerEntity player) {
		if (has(player.getDisplayName())) {
			playersByName.remove(player.getDisplayName());

			server.onPlayerLeave(player);
		}
	}

	public void respawn(ServerPlayerEntity player) {
		if (has(player.getDisplayName())) {
			playersByName.put(player.getDisplayName(), player);
		}
	}

	public Collection<ServerPlayerEntity> get() {
		return playersByName.values();
	}

	public ServerPlayerEntity get(String name) {
		return playersByName.get(name);
	}

	public boolean has(String name) {
		return playersByName.containsKey(name);
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

		for (ServerPlayerEntity player : playersByName.values()) {
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
