package redstone.multimeter.server.meter.log;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

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

		for (Level level : meterGroup.getMultimeter().getServer().getLevels()) {
			long gameTime = level.getGameTime();

			if (gameTime < cutoff) {
				cutoff = gameTime;
			}
		}

		subticks.keySet().removeIf(time -> time < cutoff);
	}

	private int nextSubtick(long tick) {
		return subticks.compute(tick, (key, value) -> {
			return value == null ? 0 : ++value;
		});
	}

	public void logEvent(Level level, Meter meter, MeterEvent event) {
		long tick = level.getGameTime();
		int subtick = nextSubtick(tick);
		TickPhase phase = meterGroup.getMultimeter().getServer().getTickPhase();

		meter.getLogs().add(new EventLog(tick, subtick, phase, event));

		unsentLogs++;
	}

	public void broadcastLogs() {
		if (unsentLogs == 0) {
			return;
		}

		ListTag list = new ListTag();

		for (Meter meter : meterGroup.getMeters()) {
			if (meter.getLogs().isEmpty()) {
				continue;
			}

			long id = meter.getId();
			CompoundTag logs = meter.getLogs().toNbt();

			CompoundTag nbt = new CompoundTag();
			nbt.putLong("id", id);
			nbt.put("logs", logs);
			nbt.putBoolean("powered", meter.isPowered());
			nbt.putBoolean("active", meter.isActive());
			list.add(nbt);

			meter.getLogs().clear();
		}

		if (list.isEmpty()) {
			return;
		}

		MeterLogsPacket packet = new MeterLogsPacket(list);
		meterGroup.getMultimeter().getServer().getPlayerList().send(packet, meterGroup);

		unsentLogs = 0;
	}
}
