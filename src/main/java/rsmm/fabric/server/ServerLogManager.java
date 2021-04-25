package rsmm.fabric.server;

import java.util.Arrays;

import net.minecraft.nbt.CompoundTag;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.event.TickPhase;
import rsmm.fabric.common.log.LogManager;

public class ServerLogManager extends LogManager {
	
	private final ServerMeterGroup meterGroup;
	private final int[] tickPhaseLogs;
	
	private int currentSubTick;
	
	public ServerLogManager(ServerMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.tickPhaseLogs = new int[TickPhase.PHASES.length];
		
		Arrays.fill(tickPhaseLogs, 0);
	}
	
	@Override
	protected MeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getCurrentTick() {
		return meterGroup.getMultimeter().getMultimeterServer().getMinecraftServer().getTicks();
	}
	
	public void tick() {
		currentSubTick = 0;
		onTickPhase(TickPhase.UNKNOWN);
	}
	
	public void onTickPhase(TickPhase phase) {
		tickPhaseLogs[phase.getIndex()] = currentSubTick;
	}
	
	public void logEvent(Meter meter, EventType type, int metaData) {
		if (meter.isMetering(type)) {
			MeterEvent event = new MeterEvent(type, getCurrentTick(), currentSubTick++, metaData);
			meter.getLogs().add(event);
		}
		
		meter.markDirty();
	}
	
	public CompoundTag collectMeterData() {
		CompoundTag data = new CompoundTag();
		
		int subTickCount = currentSubTick;
		int meterCount = meterGroup.getMeterCount();
		
		data.putInt("subTickCount", subTickCount);
		data.putIntArray("tickPhaseLogs", tickPhaseLogs);
		
		for (int index = 0; index < meterCount; index++) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter.isDirty()) {
				String key = String.valueOf(index);
				CompoundTag meterData = meter.collectData();
				
				data.put(key, meterData);
			}
		}
		
		return data;
	}
}
