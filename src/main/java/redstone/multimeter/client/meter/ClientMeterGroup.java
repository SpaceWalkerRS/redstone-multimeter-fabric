package redstone.multimeter.client.meter;

import java.util.List;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import net.minecraft.nbt.NbtCompound;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.log.ClientLogManager;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.meter.MeterProperties;

public class ClientMeterGroup extends MeterGroup {
	
	private final MultimeterClient client;
	private final ClientLogManager logManager;
	
	private String name;
	private int nextIndex;
	
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
	
	public int getNextMeterIndex() {
		return nextIndex;
	}
	
	public boolean hasMeter(Meter meter) {
		return hasMeter(meter.getId());
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
		meterUpdated(meter);
	}
	
	public void update(String newName, NbtCompound nbt) {
		name = newName;
		updateFromNBT(nbt);
		
		logManager.getPrinter().onNewMeterGroup();
		client.getHUD().updateMeterList();
	}
	
	public void reset() {
		name = super.getName();
		logManager.getPrinter().stop();
		clear();
	}
}
