package rsmm.fabric.server.meter.log;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import rsmm.fabric.common.TickPhase;
import rsmm.fabric.common.meter.Meter;
import rsmm.fabric.common.meter.event.EventType;
import rsmm.fabric.common.meter.event.MeterEvent;
import rsmm.fabric.common.meter.log.LogManager;
import rsmm.fabric.common.network.packets.MeterLogsPacket;
import rsmm.fabric.server.meter.ServerMeterGroup;

public class ServerLogManager extends LogManager {
	
	private final ServerMeterGroup meterGroup;
	
	private int nextSubTick;
	
	public ServerLogManager(ServerMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
	
	@Override
	protected ServerMeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getLastTick() {
		return meterGroup.getMultimeter().getMultimeterServer().getMultimeter().getCurrentTick();
	}
	
	@Override
	public void clearLogs() {
		super.clearLogs();
		
		nextSubTick = 0;
	}
	
	public void tick() {
		nextSubTick = 0;
	}
	
	public void logEvent(Meter meter, EventType type, int metaData) {
		if (!meter.isMetering(type)) {
			return;
		}
		
		long tick = getLastTick();
		int subTick = nextSubTick++;
		TickPhase phase = meterGroup.getMultimeter().getCurrentTickPhase();
		
		MeterEvent event = new MeterEvent(type, tick, subTick, phase, metaData);
		meter.getLogs().add(event);
	}
	
	public void flushLogs() {
		if (nextSubTick == 0) {
			return;
		}
		
		NbtList list = new NbtList();
		
		for (Meter meter : meterGroup.getMeters()) {
			if (meter.getLogs().isEmpty()) {
				continue;
			}
			
			long id = meter.getId();
			NbtCompound logs = meter.getLogs().toNBT();
			
			NbtCompound nbt = new NbtCompound();
			nbt.putLong("id", id);
			nbt.put("logs", logs);
			nbt.putBoolean("powered", meter.isPowered());
			nbt.putBoolean("active", meter.isActive());
			list.add(nbt);
			
			meter.getLogs().clear();
		}
		
		if (list.isEmpty()) {
			return;
		}
		
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("subTickCount", nextSubTick);
		nbt.put("logs", list);
		
		MeterLogsPacket packet = new MeterLogsPacket(nbt);
		meterGroup.getMultimeter().getMultimeterServer().getPacketHandler().sendPacketToPlayers(packet, meterGroup.getSubscribers());
	}
}
