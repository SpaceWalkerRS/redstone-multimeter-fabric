package rsmm.fabric.client;

import java.util.List;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import net.minecraft.nbt.NbtCompound;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.MeterProperties;

public class ClientMeterGroup extends MeterGroup {
	
	private final MultimeterClient client;
	private final ClientLogManager logManager;
	
	private String name;
	
	public ClientMeterGroup(MultimeterClient client) {
		super(client.getMinecraftClient().getSession().getUsername());
		
		this.client = client;
		this.logManager = new ClientLogManager(this);
		
		this.name = super.getName();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void clear() {
		super.clear();
		updateUI();
	}
	
	@Override
	protected void meterAdded(Meter meter) {
		updateUI();
	}
	
	@Override
	protected void meterRemoved(Meter meter) {
		updateUI();
	}
	
	@Override
	protected void meterUpdated(Meter meter) {
		updateUI();
	}
	
	@Override
	public ClientLogManager getLogManager() {
		return logManager;
	}
	
	public MultimeterClient getMultimeterClient() {
		return client;
	}
	
	public void updateMeters(List<Long> removedMeters, Long2ObjectMap<MeterProperties> meterUpdates) {
		for (int index = 0; index < removedMeters.size(); index++) {
			long id = removedMeters.get(index);
			Meter meter = getMeter(id);
			
			if (meter != null) {
				removeMeter(meter);
			}
		}
		for (Entry<MeterProperties> entry : meterUpdates.long2ObjectEntrySet()) {
			long id = entry.getLongKey();
			MeterProperties newProperties = entry.getValue();
			Meter meter = getMeter(id);
			
			if (meter == null) {
				addMeter(new Meter(id, newProperties));
			} else {
				updateMeter(meter, newProperties);
			}
		}
	}
	
	public void toggleHidden(Meter meter) {
		meter.toggleHidden();
		updateUI();
	}
	
	public void update(String newName, NbtCompound nbt) {
		name = newName;
		updateFromNBT(nbt);
		updateUI();
	}
	
	public void reset() {
		name = super.getName();
		clear();
	}
	
	private void updateUI() {
		if (client.hasMultimeterScreenOpen()) {
			client.getScreen().update();
		} else if (client.shouldRenderHud()) {
			client.getHudRenderer().updateRowCount();
		}
	}
}
