package rsmm.fabric.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.log.LogManager;

public class ClientLogManager extends LogManager {
	
	private static final long AGE_CUTOFF = 10000L;
	private static final long MAX_LOG_AGE = 1000000L;
	
	private final ClientMeterGroup meterGroup;
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
	
	public void updateMeterLogs(PacketByteBuf data) {
		subTickCount.put(getCurrentTick(), data.readInt());
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.readLogs(data);
		}
	}
	
	public void clearOldLogs() {
		long selectedTickCutoff = meterGroup.getMultimeterClient().getSelectedTick() - AGE_CUTOFF;
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
