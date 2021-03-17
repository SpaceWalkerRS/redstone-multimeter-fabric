package rsmm.fabric.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.task.MultimeterTask;

public class Multimeter {
	
	private final Map<String, MeterGroup> meterGroups;
	private final Map<PlayerEntity, MeterGroup> subscriptions;
	private final List<MeterGroup> queuedMeterGroups;
	private final Set<MultimeterTask> scheduledTasks;
	
	private long currentTick;
	
	public Multimeter() {
		this.meterGroups = new LinkedHashMap<>();
		this.subscriptions = new HashMap<>();
		this.queuedMeterGroups = new LinkedList<>();
		this.scheduledTasks = new LinkedHashSet<>();
		
		// FOR TESTING ONLY - REMOVE THIS
		meterGroups.put("group 1", new MeterGroup("group 1"));
		meterGroups.put("group 2", new MeterGroup("group 2"));
		meterGroups.put("group 3", new MeterGroup("group 3"));
	}
	
	public Set<String> getMeterGroupNames() {
		return Collections.unmodifiableSet(meterGroups.keySet());
	}
	
	public MeterGroup getMeterGroup(String name) {
		return meterGroups.get(name);
	}
	
	public void addMeterGroup(MeterGroup meterGroup) {
		meterGroups.put(meterGroup.getName(), meterGroup);
		meterGroup.init(currentTick);
	}
	
	// For use on the client only. The client only receives data
	// for the MeterGroup it is subscribed to, so it is best to
	// remove others.
	public void removeMeterGroup(MeterGroup meterGroup) {
		meterGroups.remove(meterGroup.getName(), meterGroup);
		
		for (PlayerEntity player : meterGroup.getSubscribers()) {
			subscriptions.remove(player, meterGroup);
		}
	}
	
	public MeterGroup getSubscription(PlayerEntity player) {
		return subscriptions.get(player);
	}
	
	public void addSubscription(PlayerEntity player, MeterGroup meterGroup) {
		MeterGroup prevSubscription = subscriptions.put(player, meterGroup);
		
		if (prevSubscription != null) {
			prevSubscription.removeSubscriber(player);
		}
		
		meterGroup.addSubscriber(player);
	}
	
	public void removeSubscription(PlayerEntity player, MeterGroup meterGroup) {
		if (subscriptions.remove(player, meterGroup)) {
			meterGroup.removeSubscriber(player);
		}
	}
	
	public void removeSubscription(PlayerEntity player) {
		MeterGroup meterGroup = subscriptions.remove(player);
		
		if (meterGroup != null) {
			meterGroup.removeSubscriber(player);
		}
	}
	
	public void scheduleTask(MultimeterTask task) {
		scheduledTasks.add(task);
	}
	
	private void runTasks() {
		for (MultimeterTask task : scheduledTasks) {
			task.run(null);
		}
		
		scheduledTasks.clear();
	}
	
	public void tick(long currentTick) {
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.tick();
		}
		
		runTasks();
		
		for (MeterGroup meterGroup : queuedMeterGroups) {
			if (meterGroups.put(meterGroup.getName(), meterGroup) == null) {
				meterGroup.init(currentTick);
			}
		}
		
		queuedMeterGroups.clear();
	}
	
	public void clearLogs() {
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.clearLogs();
		}
	}
	
	public void blockUpdate(WorldPos pos, boolean powered) {
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.blockUpdate(pos, powered);
		}
	}
	
	public void blockUpdate(World world, BlockPos pos, boolean powered) {
		blockUpdate(new WorldPos(world, pos), powered);
	}
	
	public void stateChanged(WorldPos pos, boolean active) {
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.stateChanged(pos, active);
		}
	}
	
	public void stateChanged(World world, BlockPos pos, boolean active) {
		stateChanged(new WorldPos(world, pos), active);
	}
}
