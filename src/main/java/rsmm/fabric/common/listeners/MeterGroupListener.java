package rsmm.fabric.common.listeners;

import rsmm.fabric.common.MeterGroup;

public interface MeterGroupListener {
	
	public void cleared(MeterGroup meterGroup);
	
	public void meterAdded(MeterGroup meterGroup, int index);
	
	public void meterRemoved(MeterGroup meterGroup, int index);
	
}
