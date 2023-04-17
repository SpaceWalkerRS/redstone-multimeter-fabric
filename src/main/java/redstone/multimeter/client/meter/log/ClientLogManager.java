package redstone.multimeter.client.meter.log;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.log.EventLog;
import redstone.multimeter.common.meter.log.LogManager;
import redstone.multimeter.common.meter.log.MeterLogs;

public class ClientLogManager extends LogManager {

	/** The maximum age relative to the selected tick */
	private static final long AGE_CUTOFF = 10000L;
	/** The maximum age relative to the current server tick */
	private static final long MAX_LOG_AGE = 1000000L;

	private final ClientMeterGroup meterGroup;
	/** The number of logged events in any tick */
	private final Long2IntMap subticks;
	private final LogPrinter printer;

	public ClientLogManager(ClientMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.subticks = new Long2IntLinkedOpenHashMap();
		this.printer = new LogPrinter(this);
	}

	@Override
	protected ClientMeterGroup getMeterGroup() {
		return meterGroup;
	}

	@Override
	public void clearLogs() {
		super.clearLogs();
		subticks.clear();
	}

	public LogPrinter getPrinter() {
		return printer;
	}

	public int getSubtickCount(long tick) {
		return subticks.getOrDefault(tick, -1) + 1;
	}

	public void tick() {
		clearOldLogs();
		printer.tick();
	}

	private void clearOldLogs() {
		MultimeterClient client = meterGroup.getMultimeterClient();

		long selectedTickCutoff = client.getHud().getSelectedTick() - AGE_CUTOFF;
		long serverTickCutoff = client.getPrevGameTime() - MAX_LOG_AGE;
		long cutoff = (selectedTickCutoff > serverTickCutoff) ? selectedTickCutoff : serverTickCutoff;

		subticks.long2IntEntrySet().removeIf(e -> e.getLongKey() < cutoff);

		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().clearOldLogs(cutoff);
		}
	}

	/**
	 * Log all events from the past server tick
	 */
	public void updateMeterLogs(ListTag data) {
		for (int index = 0; index < data.size(); index++) {
			CompoundTag nbt = data.getCompound(index);

			long id = nbt.getLong("id");
			Meter meter = meterGroup.getMeter(id);

			if (meter != null) {
				CompoundTag logs = nbt.getCompound("logs");
				boolean powered = nbt.getBoolean("powered");
				boolean active = nbt.getBoolean("active");

				meter.setPowered(powered);
				meter.setActive(active);

				for (EventLog log : MeterLogs.fromNbt(logs)) {
					long tick = log.getTick();
					int subtick = log.getSubtick();

					subticks.compute(tick, (key, value) -> {
						return (value == null || value < subtick) ? subtick : value;
					});

					meter.getLogs().add(log);
				}
			}
		}

		printer.printLogs();
	}
}
