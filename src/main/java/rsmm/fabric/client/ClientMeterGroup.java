package rsmm.fabric.client;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import net.minecraft.nbt.NbtCompound;

import rsmm.fabric.client.listeners.MeterGroupListener;
import rsmm.fabric.client.listeners.MeterListener;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.MeterProperties;

public class ClientMeterGroup extends MeterGroup {
	
	private final MultimeterClient client;
	private final ClientLogManager logManager;
	private final Set<MeterListener> meterListeners;
	private final Set<MeterGroupListener> meterGroupListeners;
	
	private String name;
	
	public ClientMeterGroup(MultimeterClient client) {
		super(client.getMinecraftClient().getSession().getUsername());
		
		this.client = client;
		this.logManager = new ClientLogManager(this);
		this.meterListeners = new LinkedHashSet<>();
		this.meterGroupListeners = new LinkedHashSet<>();
		
		this.name = super.getName();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void clear() {
		super.clear();
		
		meterGroupListeners.forEach(listener -> listener.meterGroupCleared(this));
	}
	
	@Override
	protected void meterPosChanged(Meter meter) {
		meterListeners.forEach(listener -> listener.posChanged(meter));
	}
	
	@Override
	protected void meterNameChanged(Meter meter) {
		meterListeners.forEach(listener -> listener.nameChanged(meter));
	}
	
	@Override
	protected void meterColorChanged(Meter meter) {
		meterListeners.forEach(listener -> listener.colorChanged(meter));
	}
	
	@Override
	protected void meterMovableChanged(Meter meter) {
		meterListeners.forEach(listener -> listener.movableChanged(meter));
	}
	
	@Override
	protected void meterEventTypesChanged(Meter meter) {
		meterListeners.forEach(listener -> listener.eventTypesChanged(meter));
	}
	
	@Override
	public ClientLogManager getLogManager() {
		return logManager;
	}
	
	public MultimeterClient getMultimeterClient() {
		return client;
	}
	
	public void addMeterListener(MeterListener listener) {
		meterListeners.add(listener);
	}
	
	public void removeMeterListener(MeterListener listener) {
		meterListeners.remove(listener);
	}
	
	public void addMeterGroupListener(MeterGroupListener listener) {
		meterGroupListeners.add(listener);
	}
	
	public void removeMeterGroupListener(MeterGroupListener listener) {
		meterGroupListeners.remove(listener);
	}
	
	public void updateMeters(List<Long> removedMeters, Long2ObjectMap<MeterProperties> meterUpdates) {
		for (int index = 0; index < removedMeters.size(); index++) {
			long id = removedMeters.get(index);
			Meter meter = getMeter(id);
			
			if (meter != null && removeMeter(meter)) {
				meterGroupListeners.forEach(listener -> listener.meterRemoved(this, id));
			}
		}
		for (Entry<MeterProperties> entry : meterUpdates.long2ObjectEntrySet()) {
			long id = entry.getLongKey();
			MeterProperties newProperties = entry.getValue();
			Meter meter = getMeter(id);
			
			if (meter == null) {
				if (addMeter(new Meter(id, newProperties))) {
					meterGroupListeners.forEach(listener -> listener.meterAdded(this, id));
				}
			} else {
				updateMeter(meter, newProperties);
			}
		}
	}
	
	public void toggleHidden(Meter meter) {
		meter.toggleHidden();
		meterListeners.forEach(listener -> listener.hiddenChanged(meter));
	}
	
	public void update(String newName, NbtCompound nbt) {
		name = newName;
		updateFromNBT(nbt);
		meterGroupListeners.forEach(listener -> listener.meterGroupCleared(this));
	}
	
	public void reset() {
		name = super.getName();
		clear();
	}
}
