package rsmm.fabric.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.log.LogManager;

public class ClientLogManager extends LogManager {
	
	/** The maximum age relative to the selected tick */
	private static final long AGE_CUTOFF = 10000L;
	/** The maximum age relative to the current server tick */
	private static final long MAX_LOG_AGE = 1000000L;
	
	private final ClientMeterGroup meterGroup;
	/** The number of logged events in any tick */
	private final Map<Long, Integer> subTicks;
	private final LogPrinter printer;
	
	public ClientLogManager(ClientMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.subTicks = new LinkedHashMap<>();
		this.printer = new LogPrinter(this);
	}
	
	@Override
	protected ClientMeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getLastTick() {
		return meterGroup.getMultimeterClient().getLastServerTick();
	}
	
	@Override
	public void clearLogs() {
		super.clearLogs();
		
		subTicks.clear();
	}
	
	public LogPrinter getPrinter() {
		return printer;
	}
	
	public int getSubTickCount(long tick) {
		return subTicks.getOrDefault(tick, 0);
	}
	
	/**
	 * Log all events from the past server tick
	 */
	public void updateMeterLogs(NbtCompound data) {
		int subTickCount = data.getInt("subTickCount");
		subTicks.put(getLastTick(), subTickCount);
		
		NbtList list = data.getList("logs", 10);
		
		for (int index = 0; index < list.size(); index++) {
			NbtCompound nbt = list.getCompound(index);
			
			long id = nbt.getLong("id");
			Meter meter = meterGroup.getMeter(id);
			
			if (meter != null) {
				NbtCompound logs = nbt.getCompound("logs");
				boolean powered = nbt.getBoolean("powered");
				boolean active = nbt.getBoolean("active");
				
				meter.getLogs().updateFromNBT(logs);
				meter.setPowered(powered);
				meter.setActive(active);
			}
		}
		
		printer.printLogs();
	}
	
	/**
	 * Remove all logged events that are too old
	 */
	public void clearOldLogs() {
		long selectedTickCutoff = meterGroup.getMultimeterClient().getHUD().getSelectedTick() - AGE_CUTOFF;
		long serverTickCutoff = getLastTick() - MAX_LOG_AGE;
		long cutoff = (selectedTickCutoff > serverTickCutoff) ? selectedTickCutoff : serverTickCutoff;
		
		Iterator<Long> it = subTicks.keySet().iterator();
		
		while (it.hasNext()) {
			if (it.next() > cutoff) {
				break;
			}
			
			it.remove();
		}
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().clearOldLogs(cutoff);
		}
	}
}
