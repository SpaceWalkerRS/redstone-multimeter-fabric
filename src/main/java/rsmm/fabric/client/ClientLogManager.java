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
	protected long getCurrentTick() {
		return meterGroup.getMultimeterClient().getCurrentServerTick();
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
	public void updateMeterLogs(CompoundTag data) {
		long currentTick = getCurrentTick();
		int meterCount = meterGroup.getMeterCount();
		
		int subTicks = data.getInt("subTickCount");
		subTickCount.put(currentTick, subTicks);
		
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
		long selectedTickCutoff = meterGroup.getMultimeterClient().getHudRenderer().getSelectedTick() - AGE_CUTOFF;
		long serverTickCutoff = getCurrentTick() - MAX_LOG_AGE;
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
