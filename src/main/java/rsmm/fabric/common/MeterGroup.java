package rsmm.fabric.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.util.PacketUtils;

public class MeterGroup {
	
	protected final String name;
	protected final List<Meter> meters;
	protected final Map<WorldPos, Integer> posToIndex;
	
	protected MeterGroup(String name) {
		this.name = name;
		this.meters = new ArrayList<>();
		this.posToIndex = new HashMap<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void clear() {
		meters.clear();
		posToIndex.clear();
	}
	
	public List<Meter> getMeters() {
		return Collections.unmodifiableList(meters);
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
	
	public Meter getMeterAt(WorldPos pos) {
		if (posToIndex.containsKey(pos)) {
			return getMeter(posToIndex.get(pos));
		}
		
		return null;
	}
	
	public boolean hasMeterAt(WorldPos pos) {
		return posToIndex.containsKey(pos);
	}
	
	public int indexOfMeterAt(WorldPos pos) {
		return posToIndex.getOrDefault(pos, -1);
	}
	
	public void addMeter(Meter meter) {
		posToIndex.put(meter.getPos(), meters.size());
		meters.add(meter);
	}
	
	public boolean removeMeter(Meter meter) {
		WorldPos pos = meter.getPos();
		
		if (posToIndex.containsKey(pos)) {
			int index = posToIndex.remove(pos);
			meters.remove(index);
			
			int meterCount = meters.size();
			
			for (int i = index; i < meterCount; i++) {
				int newIndex = i;
				meter = meters.get(newIndex);
				
				posToIndex.compute(meter.getPos(), (worldPos, oldIndex) -> newIndex);
			}
			
			return true;
		}
		
		return false;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(meters.size());
		
		for (Meter meter : meters) {
			PacketUtils.writeMeter(buffer, meter);
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		int meterCount = buffer.readInt();
		
		for (int i = 0; i < meterCount; i++) {
			Meter meter = PacketUtils.readMeter(buffer);
			
			if (meter != null) {
				addMeter(meter);
			}
		}
	}
	
	public void updateFromData(PacketByteBuf data) {
		clear();
		decode(data);
	}
}
