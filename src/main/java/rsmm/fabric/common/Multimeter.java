package rsmm.fabric.common;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.common.task.MultimeterTask;

public class Multimeter {
	
	private final Map<String, MeterGroup> meterGroups;
	private final Map<PlayerEntity, MeterGroup> subscriptions;
	private final Map<MeterGroup, Set<MultimeterTask>> scheduledTasks;
	private final Map<MeterGroup, List<MultimeterTask>> loggedTasks;
	
	private long currentTick;
	
	public Multimeter() {
		this.meterGroups = new LinkedHashMap<>();
		this.subscriptions = new HashMap<>();
		this.scheduledTasks = new LinkedHashMap<>();
		this.loggedTasks = new LinkedHashMap<>();
		
		// FOR TESTING ONLY - REMOVE THIS
		meterGroups.put("group 1", new MeterGroup("group 1"));
		meterGroups.put("group 2", new MeterGroup("group 2"));
		meterGroups.put("group 3", new MeterGroup("group 3"));
	}
	
	public Set<String> getMeterGroupNames() {
		return Collections.unmodifiableSet(meterGroups.keySet());
	}
	
	public Collection<MeterGroup> getMeterGroups() {
		return Collections.unmodifiableCollection(meterGroups.values());
	}
	
	public MeterGroup getMeterGroup(String name) {
		return meterGroups.get(name);
	}
	
	public void addMeterGroup(MeterGroup meterGroup) {
		meterGroups.put(meterGroup.getName(), meterGroup);
		meterGroup.syncTime(currentTick);
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
	
	public Set<PlayerEntity> getPlayers() {
		return subscriptions.keySet();
	}
	
	public boolean hasSubscription(PlayerEntity player) {
		return subscriptions.containsKey(player);
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
	
	public void scheduleTask(MultimeterTask task, MeterGroup meterGroup) {
		if (meterGroup == null) {
			return;
		}
		
		Set<MultimeterTask> tasks = scheduledTasks.get(meterGroup);
		
		if (tasks == null) {
			tasks = new LinkedHashSet<>();
			scheduledTasks.put(meterGroup, tasks);
		}
		
		tasks.add(task);
	}
	
	private void runTasks() {
		loggedTasks.clear();
		
		for (Entry<MeterGroup, Set<MultimeterTask>> entry : scheduledTasks.entrySet()) {
			MeterGroup meterGroup = entry.getKey();
			Set<MultimeterTask> tasks = entry.getValue();
			
			for (MultimeterTask task : tasks) {
				runTask(task, meterGroup);
			}
		}
		
		scheduledTasks.clear();
	}
	
	public void runTask(MultimeterTask task, MeterGroup meterGroup) {
		if (task.run(meterGroup)) {
			logTask(task, meterGroup);
		}
	}
	
	private void logTask(MultimeterTask task, MeterGroup meterGroup) {
		List<MultimeterTask> tasks = loggedTasks.get(meterGroup);
		
		if (tasks == null) {
			tasks = new LinkedList<>();
			loggedTasks.put(meterGroup, tasks);
		}
		
		tasks.add(task);
	}
	
	public Map<MeterGroup, List<MultimeterTask>> getLoggedTasks() {
		return loggedTasks;
	}
	
	public void clearTaskLogs() {
		loggedTasks.clear();
	}
	
	public long getTime() {
		return currentTick;
	}
	
	public void tick() {
		currentTick++;
		
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.tick();
		}
		
		runTasks();
	}
	
	public void syncTime(long currentTick) {
		this.currentTick = currentTick;
		
		for (MeterGroup meterGroup : meterGroups.values()) {
			meterGroup.syncTime(currentTick);
		}
	}
	
	public void clearMeterLogs() {
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
