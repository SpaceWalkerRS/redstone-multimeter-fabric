package rsmm.fabric.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.interfaces.mixin.IBlock;

public class ServerMeterGroup extends MeterGroup {
	
	private final Multimeter multimeter;
	private final Map<WorldPos, Integer> posToIndex;
	private final Set<ServerPlayerEntity> subscribers;
	private final ServerLogManager logManager;
	
	private int totalMeterCount; // The total number of meters ever added to this group
	
	public ServerMeterGroup(Multimeter multimeter, String name) {
		super(name);
		
		this.multimeter = multimeter;
		this.posToIndex = new HashMap<>();
		this.subscribers = new HashSet<>();
		this.logManager = new ServerLogManager(this);
	}
	
	@Override
	public void clear() {
		super.clear();
		
		posToIndex.clear();
		totalMeterCount = 0;
	}
	
	@Override
	public boolean addMeter(Meter meter) {
		if (super.addMeter(meter)) {
			posToIndex.put(meter.getPos(), meters.size() - 1);
			totalMeterCount++;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean removeMeter(int index) {
		Meter meter = getMeter(index);
		
		if (meter != null && super.removeMeter(index)) {
			int meterCount = meters.size();
			
			if (meterCount == 0) {
				totalMeterCount = 0;
			}
			
			posToIndex.remove(meter.getPos());
			
			for (;index < meterCount; index++) {
				int newIndex = index;
				meter = meters.get(newIndex);
				
				posToIndex.compute(meter.getPos(), (pos, oldIndex) -> newIndex);
			}
			
			return true;
		}
		
		return false;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public Meter getMeterAt(WorldPos pos) {
		return getMeter(indexOfMeterAt(pos));
	}
	
	public boolean hasMeterAt(WorldPos pos) {
		return posToIndex.containsKey(pos);
	}
	
	public int indexOfMeterAt(WorldPos pos) {
		return posToIndex.getOrDefault(pos, -1);
	}
	
	public int removeMeterAt(WorldPos pos) {
		int index = indexOfMeterAt(pos);
		return removeMeter(index) ? index : -1;
	}
	
	public void tryMoveMeter(int index, WorldPos toPos) {
		tryMoveMeter(index, toPos, false);
	}
	
	public void tryMoveMeter(int index, WorldPos toPos, boolean force) {
		if (index < 0 || index >= meters.size() || posToIndex.containsKey(toPos)) {
			return;
		}
		
		Meter meter = meters.get(index);
		
		if (!meter.isMovable() && !force) {
			return;
		}
		
		posToIndex.remove(meter.getPos());
		posToIndex.put(toPos, index);
		
		meter.setPos(toPos);
		meter.markDirty();
	}
	
	public String getNextMeterName(Block block) {
		String name = "Meter";
		
		if (block != null && ((IBlock)block).isMeterable()) {
			Identifier id = Registry.BLOCK.getId(block);
			name = id.getPath();
		}
		
		return String.format("%s %d", name, totalMeterCount);
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
	
	public ServerLogManager getLogManager() {
		return logManager;
	}
	
	/**
	 * Check if this meter group has changes that need to be synced with clients
	 */
	public boolean isDirty() {
		for (Meter meter : meters) {
			if (meter.isDirty()) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check if this meter group has new logs that need to be sent to clients
	 */
	public boolean hasNewLogs() {
		for (Meter meter : meters) {
			if (meter.hasNewLogs()) {
				return true;
			}
		}
		
		return false;
	}
	
	public CompoundTag collectMeterChanges() {
		CompoundTag meterChanges = new CompoundTag();
		
		int meterCount = getMeterCount();
		
		for (int index = 0; index < meterCount; index++) {
			Meter meter = getMeter(index);
			
			if (meter.isDirty()) {
				String key = String.valueOf(index);
				CompoundTag meterData = meter.toTag();
				
				meterChanges.put(key, meterData);
			}
		}
		
		return meterChanges;
	}
	
	public void cleanUp() {
		for (Meter meter : meters) {
			if (meter.isDirty()) {
				meter.cleanUp();
			}
		}
	}
	
	public void cleanLogs() {
		for (Meter meter : meters) {
			if (meter.hasNewLogs()) {
				meter.cleanLogs();
			}
		}
	}
	
	public void blockChanged(WorldPos pos, Block oldBlock , Block newBlock) {
		Meter meter = getMeterAt(pos);
		
		if (meter != null && oldBlock != Blocks.MOVING_PISTON && newBlock != Blocks.MOVING_PISTON) {
			int oldBlockDefaults = ((IBlock)oldBlock).getDefaultMeteredEvents();
			int newBlockDefaults = ((IBlock)newBlock).getDefaultMeteredEvents();
			
			if (meter.getMeteredEventTypes() == oldBlockDefaults) {
				meter.setMeteredEventTypes(newBlockDefaults);
				meter.markDirty();
			}
		}
	}
	
	public void tryLogEvent(WorldPos pos, EventType type, int metaData, Predicate<Meter> meterPredicate,BiConsumer<ServerMeterGroup, Integer> onLog) {
		int index = indexOfMeterAt(pos);
		
		if (index >= 0) {
			Meter meter = getMeter(index);
			
			if (meterPredicate.test(meter)) {
				onLog.accept(this, index);
				logManager.logEvent(meter, type, metaData);
			}
		}
	}
}
