package rsmm.fabric.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.CompoundTag;

import rsmm.fabric.common.listeners.MeterGroupChangeDispatcher;

public class MeterGroup {
	
	protected final String name;
	protected final List<Meter> meters;
	
	protected MeterGroup(String name) {
		this.name = name;
		this.meters = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void clear() {
		meters.clear();
		MeterGroupChangeDispatcher.cleared(this);
	}
	
	public List<Meter> getMeters() {
		return Collections.unmodifiableList(meters);
	}
	
	public int getMeterCount() {
		return meters.size();
	}
	
	public boolean hasMeters() {
		return !meters.isEmpty();
	}
	
	public Meter getMeter(int index) {
		if (index >= 0 && index < meters.size()) {
			return meters.get(index);
		}
		
		return Meter.DUMMY;
	}
	
	public boolean addMeter(Meter meter) {
		if (meters.add(meter)) {
			MeterGroupChangeDispatcher.meterAdded(this, meters.size() - 1);
			return true;
		}
		
		return false;
	}
	
	public boolean removeMeter(int index) {
		if (index >= 0 && index < meters.size()) {
			if (meters.remove(index) != null) {
				MeterGroupChangeDispatcher.meterRemoved(this, index);
				return true;
			}
		}
		
		return false;
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		
		int meterCount = meters.size();
		tag.putInt("meterCount", meterCount);
		
		for (int index = 0; index < meterCount; index++) {
			Meter meter = meters.get(index);
			tag.put(String.valueOf(index), meter.toTag());
		}
		
		return tag;
	}
	
	/**
	 * Update this MeterGroup from a CompoundTag - ONLY USE THIS METHOD ON A NEW MeterGroup!
	 */
	public void fromTag(CompoundTag data) {
		int meterCount = data.getInt("meterCount");
		
		for (int index = 0; index < meterCount; index++) {
			String key = String.valueOf(index);
			
			if (data.contains(key)) {
				CompoundTag meterData = data.getCompound(key);
				Meter meter = Meter.createFromTag(meterData);
				
				addMeter(meter);
			}
		}
	}
	
	/**
	 * Update this MeterGroup from a CompoundTag
	 */
	public void updateFromTag(CompoundTag data) {
		clear();
		fromTag(data);
	}
}
