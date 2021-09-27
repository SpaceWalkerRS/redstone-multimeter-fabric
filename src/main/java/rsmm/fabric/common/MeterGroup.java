package rsmm.fabric.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import rsmm.fabric.common.log.LogManager;

public abstract class MeterGroup {
	
	private final String name;
	private final List<Meter> meters;
	private final Map<Long, Integer> idToIndex;
	private final Map<WorldPos, Integer> posToIndex;
	
	protected MeterGroup(String name) {
		this.name = name;
		this.meters = new ArrayList<>();
		this.idToIndex = new HashMap<>();
		this.posToIndex = new HashMap<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void clear() {
		meters.clear();
		idToIndex.clear();
		posToIndex.clear();
		getLogManager().clearLogs();
	}
	
	public boolean hasMeters() {
		return !meters.isEmpty();
	}
	
	public List<Meter> getMeters() {
		return Collections.unmodifiableList(meters);
	}
	
	public boolean hasMeter(long id) {
		return idToIndex.containsKey(id);
	}
	
	public boolean hasMeterAt(WorldPos pos) {
		return posToIndex.containsKey(pos);
	}
	
	public Meter getMeter(long id) {
		return fromIndex(idToIndex.getOrDefault(id, -1));
	}
	
	public Meter getMeterAt(WorldPos pos) {
		return fromIndex(posToIndex.getOrDefault(pos, -1));
	}
	
	private Meter fromIndex(int index) {
		return (index < 0 || index >= meters.size()) ? null : meters.get(index);
	}
	
	protected boolean addMeter(Meter meter) {
		// This check prevents meters from being added twice and
		// multiple meters from being added at the same position.
		if (idToIndex.containsKey(meter.getId()) || posToIndex.containsKey(meter.getPos())) {
			return false;
		}
		
		idToIndex.put(meter.getId(), meters.size());
		posToIndex.put(meter.getPos(), meters.size());
		meters.add(meter);
		
		return true;
	}
	
	protected boolean removeMeter(Meter meter) {
		int index = idToIndex.getOrDefault(meter.getId(), -1);
		
		if (index < 0 || index >= meters.size()) {
			return false;
		}
		
		meters.remove(index);
		idToIndex.remove(meter.getId(), index);
		posToIndex.remove(meter.getPos(), index);
		
		for (; index < meters.size(); index++) {
			meter = meters.get(index);
			
			idToIndex.compute(meter.getId(), (id, prevIndex) -> prevIndex - 1);
			posToIndex.compute(meter.getPos(), (pos, prevIndex) -> prevIndex - 1);
		}
		
		return true;
	}
	
	protected boolean updateMeter(Meter meter, MeterProperties newProperties) {
		return meter.applyUpdate(properties -> {
			boolean changed = false;
			
			if (newProperties.getPos() != null && moveMeter(meter, newProperties.getPos())) {
				changed = true;
				meterPosChanged(meter);
			}
			if (newProperties.getName() != null && properties.setName(newProperties.getName())) {
				changed = true;
				meterNameChanged(meter);
			}
			if (newProperties.getColor() != null && properties.setColor(newProperties.getColor())) {
				changed = true;
				meterColorChanged(meter);
			}
			if (newProperties.getMovable() != null && properties.setMovable(newProperties.getMovable())) {
				changed = true;
				meterMovableChanged(meter);
			}
			if (newProperties.getEventTypes() != null && properties.setEventTypes(newProperties.getEventTypes())) {
				changed = true;
				meterEventTypesChanged(meter);
			}
			
			return changed;
		});
	}
	
	protected boolean moveMeter(Meter meter, WorldPos newPos) {
		WorldPos pos = meter.getPos();
		
		if (pos.equals(newPos)) {
			return false;
		}
		
		int index = posToIndex.getOrDefault(pos, -1);
		
		if (index < 0 || index >= meters.size()) {
			return false;
		}
		
		posToIndex.remove(pos, index);
		posToIndex.put(newPos, index);
		
		return meter.applyUpdate(properties -> properties.setPos(newPos));
	}
	
	protected abstract void meterPosChanged(Meter meter);
	
	protected abstract void meterNameChanged(Meter meter);
	
	protected abstract void meterColorChanged(Meter meter);
	
	protected abstract void meterMovableChanged(Meter meter);
	
	protected abstract void meterEventTypesChanged(Meter meter);
	
	public abstract LogManager getLogManager();
	
	public NbtCompound toNBT() {
		NbtList list = new NbtList();
		
		for (Meter meter : meters) {
			list.add(meter.toNBT());
		}
		
		NbtCompound nbt = new NbtCompound();
		nbt.put("meters", list);
		
		return nbt;
	}
	
	public void updateFromNBT(NbtCompound nbt) {
		clear();
		
		NbtList list = nbt.getList("meters", 10);
		
		for (int index = 0; index < list.size(); index++) {
			NbtCompound meterNbt = list.getCompound(index);
			Meter meter = Meter.fromNBT(meterNbt);
			
			addMeter(meter);
		}
	}
}
