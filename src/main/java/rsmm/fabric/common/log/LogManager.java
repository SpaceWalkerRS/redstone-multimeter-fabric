package rsmm.fabric.common.log;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;

public abstract class LogManager {
	
	protected abstract MeterGroup getMeterGroup();
	
	protected abstract long getLastTick();
	
	public void clearLogs() {
		for (Meter meter : getMeterGroup().getMeters()) {
			meter.getLogs().clear();
		}
	}
}
