package redstone.multimeter.server.meter.log;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.LogManager;
import redstone.multimeter.common.network.packets.MeterLogsPacket;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class ServerLogManager extends LogManager {

	private final ServerMeterGroup meterGroup;
	private final Long2IntMap subticks;

	private long cutoff;
	private int unsentLogs;

	public ServerLogManager(ServerMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.subticks = new Long2IntLinkedOpenHashMap();

		this.cutoff = -1L;
	}

	@Override
	protected ServerMeterGroup getMeterGroup() {
		return meterGroup;
	}

	@Override
	public void clearLogs() {
		super.clearLogs();

		subticks.clear();

		cutoff = -1L;
		unsentLogs = 0;
	}

	public void tick() {
		cutoff = Long.MAX_VALUE;

		for (World world : meterGroup.getMultimeter().getServer().getWorlds()) {
			long gameTime = world.getTime();

			if (gameTime < cutoff) {
				cutoff = gameTime;
			}
		}

		subticks.long2IntEntrySet().removeIf(e -> e.getLongKey() < cutoff);
	}

	private int nextSubtick(long tick) {
		return subticks.compute(tick, (key, value) -> {
			return value == null ? 0 : ++value;
		});
	}

	public void logEvent(World world, Meter meter, MeterEvent event) {
		long tick = world.getTime();
		int subtick = nextSubtick(tick);
		TickPhase phase = meterGroup.getMultimeter().getServer().getTickPhase();

		meter.getLogs().add(new EventLog(tick, subtick, phase, event));

		unsentLogs++;
	}

	public void broadcastLogs() {
		if (unsentLogs == 0) {
			return;
		}

		NbtList list = new NbtList();

		for (Meter meter : meterGroup.getMeters()) {
			if (meter.getLogs().isEmpty()) {
				continue;
			}

			long id = meter.getId();
			NbtCompound logs = meter.getLogs().toNbt();

			NbtCompound nbt = new NbtCompound();
			nbt.putLong("id", id);
			nbt.put("logs", logs);
			nbt.putBoolean("powered", meter.isPowered());
			nbt.putBoolean("active", meter.isActive());
			list.add(nbt);

			meter.getLogs().clear();
		}

		if (list.size() == 0) {
			return;
		}

		MeterLogsPacket packet = new MeterLogsPacket(list);
		meterGroup.getMultimeter().getServer().getPlayerList().send(packet, meterGroup);

		unsentLogs = 0;
	}
}
