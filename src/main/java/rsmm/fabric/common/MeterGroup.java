package rsmm.fabric.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MeterGroup {
	
	private final String name;
	private final Map<BlockPos, Meter> meters;
	private final Set<PlayerEntity> subscribers;
	
	public MeterGroup(String name) {
		this.name = name;
		this.meters = new HashMap<>();
		this.subscribers = new HashSet<>();
	}
	
	public String getName() {
		return name;
	}
	
	public Map<BlockPos, Meter> getMeters() {
		return Collections.unmodifiableMap(meters);
	}
	
	public int getMeterCount() {
		return meters.size();
	}
	
	public void subscribe(PlayerEntity player) {
		subscribers.add(player);
	}
	
	public void unsubscribe(PlayerEntity player) {
		subscribers.remove(player);
	}
	
	public boolean isSubscribed(PlayerEntity player) {
		return subscribers.contains(player);
	}
	
	public void toggleMeter(BlockPos pos) {
		meters.compute(pos, (meterPos, meter) -> meter == null ? new Meter(this) : null);
	}
	
	public void blockUpdate(BlockPos pos, boolean powered) {
		Meter meter = meters.get(pos);
		
		if (meter != null) {
			meter.blockUpdate(powered);
		}
	}
	
	public void stateChanged(BlockPos pos, boolean active) {
		Meter meter = meters.get(pos);
		
		if (meter != null) {
			meter.stateChanged(active);
		}
	}
}