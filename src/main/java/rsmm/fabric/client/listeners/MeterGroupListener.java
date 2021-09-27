package rsmm.fabric.client.listeners;

import rsmm.fabric.common.MeterGroup;

public interface MeterGroupListener {
	
	public void meterGroupCleared(MeterGroup meterGroup);
	
	public void meterAdded(MeterGroup meterGroup, long id);
	
	public void meterRemoved(MeterGroup meterGroup, long id);
	
}
