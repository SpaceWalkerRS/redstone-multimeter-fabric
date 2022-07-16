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
		client.getTutorial().onMeterGroupRefreshed();
	}
	
	@Override
	protected void meterAdded(Meter meter) {
		nextIndex++;
		client.getHUD().updateMeterList();
		client.getTutorial().onMeterAdded(meter);
	}
	
	@Override
	protected void meterRemoved(Meter meter) {
		if (!hasMeters()) {
			nextIndex = 0;
		}
		
		client.getHUD().updateMeterList();
		client.getTutorial().onMeterRemoved(meter);
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
				addMeter(new Meter(id, newProperties.toMutable()));
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
		client.getTutorial().onJoinMeterGroup();
	}
	
	public void unsubscribe(boolean disconnect) {
		subscribed = false;
		name = super.getName();
		logManager.getPrinter().stop(!disconnect);
		clear();
		
		client.getTutorial().onLeaveMeterGroup();
	}
	
	public void refresh(NbtCompound nbt) {
		updateFromNbt(nbt);
		client.getHUD().updateMeterList();
		client.getTutorial().onMeterGroupRefreshed();
	}
	
	public void tick() {
		logManager.tick();
	}
}
