package redstone.multimeter.server.meter.log;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.meter.log.LogManager;
import redstone.multimeter.common.network.packets.MeterLogsPacket;
import redstone.multimeter.server.meter.ServerMeterGroup;

public class ServerLogManager extends LogManager {
	
	private final ServerMeterGroup meterGroup;
	
	private int nextSubtick;
	
	public ServerLogManager(ServerMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
	
	@Override
	protected ServerMeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getLastTick() {
		return meterGroup.getMultimeter().getMultimeterServer().getCurrentTick();
	}
	
	@Override
	public void clearLogs() {
		super.clearLogs();
		
		nextSubtick = 0;
	}
	
	public void tick() {
		nextSubtick = 0;
	}
	
	public void logEvent(Meter meter, EventType type, int metaData) {
		if (!meter.isMetering(type)) {
			return;
		}
		
		long tick = getLastTick();
		int subtick = nextSubtick++;
		TickPhase phase = meterGroup.getMultimeter().getMultimeterServer().getCurrentTickPhase();
		
		MeterEvent event = new MeterEvent(type, tick, subtick, phase, metaData);
		meter.getLogs().add(event);
	}
	
	public void flushLogs() {
		if (nextSubtick == 0) {
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
		nbt.putInt("subtickCount", nextSubtick);
		nbt.put("logs", list);
		
		MeterLogsPacket packet = new MeterLogsPacket(nbt);
		meterGroup.getMultimeter().getMultimeterServer().getPacketHandler().sendToSubscribers(packet, meterGroup);
	}
}
