package rsmm.fabric.common.log;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.event.ActiveEvent;
import rsmm.fabric.common.event.MovedEvent;
import rsmm.fabric.common.event.PoweredEvent;

public class LogManager {
	
	private static final int MAX_LOG_AGE = 10000;
	
	private final MeterGroup meterGroup;
	private final Map<Long, Long> subTickCount;
	
	private long currentTick;
	private long currentSubTick;
	
	private long cutoff;
	
	public LogManager(MeterGroup meterGroup) {
		this.meterGroup = meterGroup;
		this.subTickCount = new HashMap<>();
	}
	
	public void tick() {
		if (currentSubTick > 0) {
			subTickCount.put(currentTick, currentSubTick);
		}
		
		currentTick++;
		currentSubTick = 0;
		
		cutoff++;
		
		if (subTickCount.remove(cutoff) != null) {
			for (Meter meter : meterGroup.getMeters()) {
				meter.getLogs().clearOldLogs(cutoff);
			}
		}
	}
	
	public void syncTime(long currentTick) {
		this.currentTick = currentTick;
		this.cutoff = this.currentTick - MAX_LOG_AGE;
	}
	
	public void clearLogs() {
		subTickCount.clear();
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.clearLogs();
		}
	}
	
	public void logPowered(Meter meter, boolean powered) {
		meter.getLogs().add(new PoweredEvent(currentTick, currentSubTick++, powered));
	}
	
	public void logActive(Meter meter, boolean active) {
		meter.getLogs().add(new ActiveEvent(currentTick, currentSubTick++, active));
	}
	
	public void logMoved(Meter meter, Direction dir) {
		meter.getLogs().add(new MovedEvent(currentTick, currentSubTick++, dir));
	}
	
	public PacketByteBuf collectMeterLogs() {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.writeLogs(data);
		}
		
		return data;
	}
	
	public void updateMeterLogs(PacketByteBuf data) {
		for (Meter meter : meterGroup.getMeters()) {
			meter.readLogs(data);
		}
	}
}
