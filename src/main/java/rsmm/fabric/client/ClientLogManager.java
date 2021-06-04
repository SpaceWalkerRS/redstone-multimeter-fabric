package rsmm.fabric.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.log.LogManager;

public class ClientLogManager extends LogManager {
	
	/** The maximum age relative to the selected tick */
	private static final long AGE_CUTOFF = 10000L;
	/** The maximum age relative to the current server tick */
	private static final long MAX_LOG_AGE = 1000000L;
	
	private final ClientMeterGroup meterGroup;
	/** The number of logged events in any tick */
	private final Map<Long, Integer> subTickCount;
	
	public ClientLogManager(ClientMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.subTickCount = new LinkedHashMap<>();
	}
	
	@Override
	protected MeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getLastTick() {
		return meterGroup.getMultimeterClient().getLastServerTick();
	}
	
	@Override
	public void clearLogs() {
		super.clearLogs();
		
		subTickCount.clear();
	}
	
	public int getSubTickCount(long tick) {
		return subTickCount.getOrDefault(tick, 0);
	}
	
	/**
	 * Log all events from the past server tick
	 */
	public void updateMeterLogs(NbtCompound data) {
		long lastTick = getLastTick();
		int meterCount = meterGroup.getMeterCount();
		
		int subTicks = data.getInt("subTickCount");
		subTickCount.put(lastTick, subTicks);
		
		for (int index = 0; index < meterCount; index++) {
			String key = String.valueOf(index);
			
			if (data.contains(key)) {
				Meter meter = meterGroup.getMeter(index);
				NbtCompound logs = data.getCompound(key);
				
				meter.getLogs().updateFromNBT(logs);
			}
		}
	}
	
	/**
	 * Remove all logged events that are too old
	 */
	public void clearOldLogs() {
		long selectedTickCutoff = meterGroup.getMultimeterClient().getHudRenderer().getSelectedTick() - AGE_CUTOFF;
		long serverTickCutoff = getLastTick() - MAX_LOG_AGE;
		long cutoff = (selectedTickCutoff > serverTickCutoff) ? selectedTickCutoff : serverTickCutoff;
		
		Iterator<Long> it = subTickCount.keySet().iterator();
		
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
