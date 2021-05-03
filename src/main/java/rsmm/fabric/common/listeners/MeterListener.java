package rsmm.fabric.common.listeners;

import rsmm.fabric.common.Meter;

public interface MeterListener {
	
	public void posChanged(Meter meter);
	
	public void nameChanged(Meter meter);
	
	public void colorChanged(Meter meter);
	
	public void isMovableChanged(Meter meter);
	
	public void meteredEventsChanged(Meter meter);
	
}
