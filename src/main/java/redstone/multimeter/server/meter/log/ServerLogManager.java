package redstone.multimeter.server.meter.log;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.meter.Meter;
import redstone.multimeter.common.meter.event.MeterEvent;
import redstone.multimeter.common.meter.log.EventLog;
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
	
	public void logEvent(Meter meter, MeterEvent event) {
		long tick = getLastTick();
		int subtick = nextSubtick++;
		TickPhase phase = meterGroup.getMultimeter().getMultimeterServer().getTickPhase();
		
		EventLog log = new EventLog(tick, subtick, phase, event);
		meter.getLogs().add(log);
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
			NbtCompound logs = meter.getLogs().toNbt();
			
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
		nbt.putInt("subticks", nextSubtick);
		nbt.put("logs", list);
		
		MeterLogsPacket packet = new MeterLogsPacket(nbt);
		meterGroup.getMultimeter().getMultimeterServer().getPacketHandler().sendToSubscribers(packet, meterGroup);
	}
}
