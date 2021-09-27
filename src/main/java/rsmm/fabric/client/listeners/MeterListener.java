package rsmm.fabric.client.listeners;

import rsmm.fabric.common.Meter;

public interface MeterListener {
	
	public void posChanged(Meter meter);
	
	public void nameChanged(Meter meter);
	
	public void colorChanged(Meter meter);
	
	public void movableChanged(Meter meter);
	
	public void eventTypesChanged(Meter meter);
	
	public void hiddenChanged(Meter meter);
	
}
