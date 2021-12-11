package redstone.multimeter.client.meter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundTag;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.log.ClientLogManager;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;

public class ClientMeterGroup extends MeterGroup {
	
	private final MultimeterClient client;
	private final ClientLogManager logManager;
	
	private boolean subscribed;
	private String name;
	private int nextIndex;
	
	public ClientMeterGroup(MultimeterClient client) {
		super("If you see this something has gone wrong...");
		
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
		nextIndex = 0;
		client.getHUD().updateMeterList();
	}
	
	@Override
	protected void meterAdded(Meter meter) {
		nextIndex++;
		client.getHUD().updateMeterList();
	}
	
	@Override
	protected void meterRemoved(Meter meter) {
		if (!hasMeters()) {
			nextIndex = 0;
		}
		
		client.getHUD().updateMeterList();
	}
	
	@Override
	protected void meterUpdated(Meter meter) {
		client.getHUD().updateMeterList();
	}
	
	@Override
	public ClientLogManager getLogManager() {
		return logManager;
	}
	
	public MultimeterClient getMultimeterClient() {
		return client;
	}
	
	public boolean isSubscribed() {
		return subscribed;
	}
	
	public int getNextMeterIndex() {
		return nextIndex;
	}
	
	public boolean hasMeter(Meter meter) {
		return hasMeter(meter.getId());
	}
	
	public void updateMeters(List<Long> removedMeters, Map<Long, MeterProperties> meterUpdates) {
		for (int index = 0; index < removedMeters.size(); index++) {
			long id = removedMeters.get(index);
			Meter meter = getMeter(id);
			
			if (meter != null) {
				removeMeter(meter);
			}
		}
		for (Entry<Long, MeterProperties> entry : meterUpdates.entrySet()) {
			long id = entry.getKey();
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
		meterUpdated(meter);
	}
	
	public void subscribe(String newName) {
		subscribed = true;
		name = newName;
		logManager.getPrinter().onNewMeterGroup();
		clear();
		
		client.refreshMeterGroup();
	}
	
	public void unsubscribe(boolean disconnect) {
		subscribed = false;
		name = super.getName();
		logManager.getPrinter().stop(!disconnect);
		clear();
	}
	
	public void refresh(CompoundTag nbt) {
		updateFromNbt(nbt);
		client.getHUD().updateMeterList();
	}
	
	public void tick() {
		logManager.tick();
	}
}
