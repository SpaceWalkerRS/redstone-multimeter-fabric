package rsmm.fabric.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.log.entry.LogType;
import rsmm.fabric.util.ColorUtils;

public class ServerMeterGroup extends MeterGroup {
	
	private final Set<ServerPlayerEntity> subscribers;
	
	private int totalMeterCount; // The total number of meters ever added to this group
	
	public ServerMeterGroup(String name) {
		super(name);
		
		this.subscribers = new HashSet<>();
	}
	
	@Override
	public void clear() {
		super.clear();
		
		totalMeterCount = 0;
	}
	
	@Override
	public void addMeter(Meter meter) {
		super.addMeter(meter);
		
		totalMeterCount++;
	}
	
	public String getNextMeterName() {
		return String.format("Meter %d", totalMeterCount);
	}
	
	public int getNextMeterColor() {
		return ColorUtils.nextColor();
	}
	
	public Set<ServerPlayerEntity> getSubscribers() {
		return Collections.unmodifiableSet(subscribers);
	}
	
	public boolean hasSubscribers() {
		return !subscribers.isEmpty();
	}
	
	public void addSubscriber(ServerPlayerEntity player) {
		subscribers.add(player);
	}
	
	public void removeSubscriber(ServerPlayerEntity player) {
		subscribers.remove(player);
	}
	
	public void blockUpdate(WorldPos pos, boolean powered) {
		Meter meter = getMeterAt(pos);
		
		if (meter != null && meter.blockUpdate(powered)) {
			logManager.log(meter, LogType.POWERED, powered);
		}
	}
	
	public void stateChanged(WorldPos pos, boolean active) {
		Meter meter = getMeterAt(pos);
		
		if (meter != null && meter.stateChanged(active)) {
			logManager.log(meter, LogType.ACTIVE, active);
		}
	}
	
	public void blockMoved(WorldPos pos, Direction dir) {
		Meter meter = getMeterAt(pos);
		
		if (meter != null) {
			if (meter.blockMoved(dir)) {
				int index = posToIndex.remove(pos);
				posToIndex.put(meter.getPos(), index);
			}
			
			logManager.log(meter, LogType.MOVED, dir);
		}
	}
}
