package rsmm.fabric.interfaces.mixin;

import rsmm.fabric.common.MeterGroup;

public interface IPlayerEntity {
	
	public MeterGroup getMeterGroup();
	
	public void subscribeToMeterGroup(MeterGroup meterGroup);
	
}
