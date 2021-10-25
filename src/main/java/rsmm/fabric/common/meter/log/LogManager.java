package rsmm.fabric.common.meter.log;

import rsmm.fabric.common.meter.Meter;
import rsmm.fabric.common.meter.MeterGroup;

public abstract class LogManager {
	
	protected abstract MeterGroup getMeterGroup();
	
	protected abstract long getLastTick();
	
	public void clearLogs() {
		for (Meter meter : getMeterGroup().getMeters()) {
			meter.getLogs().clear();
		}
	}
}
