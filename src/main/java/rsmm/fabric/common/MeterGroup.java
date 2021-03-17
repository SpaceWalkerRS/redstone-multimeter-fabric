package rsmm.fabric.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import rsmm.fabric.common.log.MeterGroupLogs;
import rsmm.fabric.util.ColorUtils;

public class MeterGroup {
	
	private final String name;
	private final List<Meter> meters;
	private final Map<WorldPos, Integer> posToIndex;
	private final Set<PlayerEntity> subscribers;
	private final MeterGroupLogs logs;
	
	
	public MeterGroup(String name) {
		this.name = name;
		this.meters = new ArrayList<>();
		this.posToIndex = new HashMap<>();
		this.subscribers = new HashSet<>();
		this.logs = new MeterGroupLogs();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MeterGroup) {
			MeterGroup meterGroup = (MeterGroup)other;
			
			return name.equals(meterGroup.name);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public String getName() {
		return name;
	}
	
	public List<Meter> getMeters() {
		return Collections.unmodifiableList(meters);
	}
	
	public void removeMeters() {
		meters.clear();
		posToIndex.clear();
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
		return posToIndex.containsKey(pos);
	}
	
	public Meter getMeterAt(WorldPos pos) {
		return getMeter(posToIndex.get(pos));
	}
	
	public void removeMeterAt(WorldPos pos) {
		Meter meter = getMeterAt(pos);
		
		if (meter != null) {
			removeMeter(meter);
		}
	}
	
	public void addMeter(Meter meter) {
		meters.add(meter);
		posToIndex.put(meter.getPos(), meters.size() - 1);
	}
	
	public void removeMeter(Meter meter) {
		WorldPos pos = meter.getPos();
		
		if (posToIndex.containsKey(pos)) {
			int index = posToIndex.get(pos);
			
			meters.remove(index);
			posToIndex.remove(pos, index);
			
			// Since we removed a meter, the pos to index mapping
			// of all subsequent meters is wrong.
			int count = meters.size();
			
			for (int i = index; i < count; i++) {
				int newIndex = i;
				Meter nextMeter = meters.get(newIndex);
				
				posToIndex.compute(nextMeter.getPos(), (meterPos, oldIndex) -> newIndex);
			}
		}
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
	
	public boolean hasSubscribers() {
		return !subscribers.isEmpty();
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
	
	public void tick() {
		logs.tick();
	}
	
	public void init(long currentTick) {
		logs.syncTime(currentTick);
	}
	
	public String nextMeterName() {
		return String.format("Meter %d", meters.size());
	}
	
	public int nextMeterColor() {
		return ColorUtils.nextColor();
	}
	
	public void blockUpdate(WorldPos pos, boolean powered) {
		int index = posToIndex.get(pos);
		
		if (index >= 0) {
			Meter meter = meters.get(index);
			
			if (meter.blockUpdate(powered)) {
				logs.meterPoweredChanged(index, powered);
			}
		}
	}
	
	public void stateChanged(WorldPos pos, boolean active) {
		int index = posToIndex.get(pos);
		
		if (index >= 0) {
			Meter meter = meters.get(index);
			
			if (meter.stateChanged(active)) {
				logs.meterActiveChanged(index, active);
			}
		}
	}
}