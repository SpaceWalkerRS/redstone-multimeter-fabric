package rsmm.fabric.common;

import java.util.Random;

import rsmm.fabric.RedstoneMultimeter;

public class Meter {
	
	private final MeterGroup meterGroup;
	
	private String name;
	private int color;
	
	private boolean powered; // true if the block is receiving power
	private boolean active;  // true if the block is emitting power
	
	public Meter(MeterGroup meterGroup, String name, int color) {
		this.meterGroup = meterGroup;
		
		this.name = name;
		this.color = color;
	}
	
	public Meter(MeterGroup meterGroup, String name) {
		this(meterGroup, name, generateColor());
	}
	
	public Meter(MeterGroup meterGroup) {
		this(meterGroup, generateName(meterGroup));
	}
	
	private static int generateColor() {
		return new Random().nextInt(16777216);
	}
	
	private static String generateName(MeterGroup meterGroup) {
		return String.format("Meter %d", meterGroup.getMeterCount());
	}
	
	public String getName() {
		return name;
	}
	
	public int getColor() {
		return color;
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void blockUpdate(boolean powered) {
		if (this.powered != powered) {
			this.powered = powered;
			
			RedstoneMultimeter.LOGGER.info(String.format("%s powered changed to %s", name, this.powered));
		}
	}
	
	public void stateChanged(boolean active) {
		if (this.active != active) {
			this.active = active;
			
			RedstoneMultimeter.LOGGER.info(String.format("%s active changed to %s", name, this.active));
		}
	}
}
