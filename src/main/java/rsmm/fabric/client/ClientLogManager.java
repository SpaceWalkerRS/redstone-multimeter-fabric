package rsmm.fabric.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.log.LogManager;

public class ClientLogManager extends LogManager {
	
	/** The maximum age relative to the selected tick */
	private static final long AGE_CUTOFF = 10000L;
	/** The maximum age relative to the current server tick */
	private static final long MAX_LOG_AGE = 1000000L;
	
	private final ClientMeterGroup meterGroup;
	/** The start times of every tick phase for every tick */
	private final Map<Long, int[]> tickPhaseLogs;
	/** The number of logged events in any tick */
	private final Map<Long, Integer> subTickCount;
	
	public ClientLogManager(ClientMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.tickPhaseLogs = new LinkedHashMap<>();
		this.subTickCount = new LinkedHashMap<>();
	}
	
	@Override
	protected MeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getCurrentTick() {
		return meterGroup.getMultimeterClient().getCurrentServerTick();
	}
	
	@Override
	public void clearLogs() {
		super.clearLogs();
		
		tickPhaseLogs.clear();
		subTickCount.clear();
	}
	
	public int getSubTickCount(long tick) {
		return subTickCount.getOrDefault(tick, 0);
	}
	
	/**
	 * Log all events from the past server tick
	 */
	public void updateMeterLogs(CompoundTag data) {
		int subTicks = data.getInt("subTickCount");
		int[] tickPhases = data.getIntArray("tickPhaseLogs");
		
		subTickCount.put(getCurrentTick(), subTicks);
		tickPhaseLogs.put(getCurrentTick(), tickPhases);
		
		int meterCount = meterGroup.getMeterCount();
		
		for (int index = 0; index < meterCount; index++) {
			String key = String.valueOf(index);
			
			if (data.contains(key)) {
				Meter meter = meterGroup.getMeter(index);
				CompoundTag meterData = data.getCompound(key);
				
				meter.updateFromData(meterData);
			}
		}
	}
	
	/**
	 * Remove all logged events that are too old
	 */
	public void clearOldLogs() {
		long selectedTickCutoff = meterGroup.getMultimeterClient().getSelectedTick() - AGE_CUTOFF;
		long serverTickCutoff = getCurrentTick() - MAX_LOG_AGE;
		
		long cutoff = (selectedTickCutoff > serverTickCutoff) ? selectedTickCutoff : serverTickCutoff;
		Iterator<Long> it1 = tickPhaseLogs.keySet().iterator();
		Iterator<Long> it2 = subTickCount.keySet().iterator();
		
		while (it1.hasNext()) {
			if (it1.next() > cutoff) {
				break;
			}
			
			it1.remove();
		}
		while (it2.hasNext()) {
			if (it2.next() > cutoff) {
				break;
			}
			
			it2.remove();
		}
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().clearOldLogs(cutoff);
		}
	}
}
