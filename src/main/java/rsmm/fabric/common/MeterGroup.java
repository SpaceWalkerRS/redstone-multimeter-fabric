package rsmm.fabric.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;

import rsmm.fabric.common.logs.MeterGroupLogs;
import rsmm.fabric.util.ColorUtils;

public class MeterGroup {
	
	private final String name;
	private final List<Meter> meters;
	private final Map<WorldPos, Meter> posToMeter;
	private final Set<PlayerEntity> subscribers;
	private final MeterGroupLogs logs;
	
	
	public MeterGroup(String name) {
		this.name = name;
		this.meters = new ArrayList<>();
		this.posToMeter = new HashMap<>();
		this.subscribers = new HashSet<>();
		this.logs = new MeterGroupLogs();
	}
	
	public String getName() {
		return name;
	}
	
	public List<Meter> getMeters() {
		return Collections.unmodifiableList(meters);
	}
	
	public void removeMeters() {
		meters.clear();
		posToMeter.clear();
	}
	
	public int getMeterCount() {
		return meters.size();
	}
	
	public Meter getMeter(int index) {
		if (index >= 0 && index < meters.size()) {
			return meters.get(index);
		}
		
		return null;
	}
	
	public boolean hasMeterAt(WorldPos pos) {
		return posToMeter.containsKey(pos);
	}
	
	public Meter getMeterAt(WorldPos pos) {
		return posToMeter.get(pos);
	}
	
	public void addMeter(Meter meter) {
		meters.add(meter);
		posToMeter.put(meter.getPos(), meter);
	}
	
	public void removeMeter(Meter meter) {
		meters.remove(meter);
		posToMeter.remove(meter.getPos(), meter);
	}
	
	public void renameMeter(int index, String name) {
		Meter meter = getMeter(index);
		
		if (meter != null) {
			meter.setName(name);
		}
	}
	
	public void recolorMeter(int index, int color) {
		Meter meter = getMeter(index);
		
		if (meter != null) {
			meter.setColor(color);
		}
	}
	
	public Set<PlayerEntity> getSubscribers() {
		return Collections.unmodifiableSet(subscribers);
	}
	
	public boolean addSubscriber(PlayerEntity player) {
		return subscribers.add(player);
	}
	
	public boolean removeSubscriber(PlayerEntity player) {
		return subscribers.remove(player);
	}
	
	public MeterGroupLogs getLogs() {
		return logs;
	}
	
	public void clearLogs() {
		logs.clear();
	}
	
	public String nextMeterName() {
		return String.format("Meter %d", meters.size());
	}
	
	public int nextMeterColor() {
		return ColorUtils.nextColor();
	}
	
	public void blockUpdate(WorldPos pos, boolean powered) {
		Meter meter = posToMeter.get(pos);
		
		if (meter != null) {
			meter.blockUpdate(powered);
		}
	}
	
	public void stateChanged(WorldPos pos, boolean active) {
		Meter meter = posToMeter.get(pos);
		
		if (meter != null) {
			meter.stateChanged(active);
		}
	}
}