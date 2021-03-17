package rsmm.fabric.common;

import net.minecraft.world.World;

import rsmm.fabric.RedstoneMultimeterMod;

public class Meter {
	
	private final WorldPos pos;
	
	private String name;
	private int color;
	
	private boolean powered; // true if the block is receiving power
	private boolean active;  // true if the block is emitting power
	
	public Meter(WorldPos pos, String name, int color, boolean initialPowered, boolean initialActive) {
		this.pos = pos;
		
		this.name = name;
		this.color = color;
		
		this.powered = initialPowered;
		this.active = initialActive;
	}
	
	public boolean isIn(World world) {
		return pos.isOf(world);
	}
	
	public WorldPos getPos() {
		return pos;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean blockUpdate(boolean powered) {
		if (this.powered != powered) {
			this.powered = powered;
			
			RedstoneMultimeterMod.LOGGER.info(String.format("%s powered changed to %s", name, this.powered));
			return true;
		}
		
		return false;
	}
	
	public boolean stateChanged(boolean active) {
		if (this.active != active) {
			this.active = active;
			
			RedstoneMultimeterMod.LOGGER.info(String.format("%s active changed to %s", name, this.active));
			return true;
		}
		
		return false;
	}
}
