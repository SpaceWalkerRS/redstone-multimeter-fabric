package rsmm.fabric.common.log;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.log.entry.ActiveChangedLog;
import rsmm.fabric.common.log.entry.BlockMovedLog;
import rsmm.fabric.common.log.entry.PoweredChangedLog;

public class MeterLogsManager {
	
	private static final int MAX_LOG_AGE = 10000;
	
	private final MeterGroup meterGroup;
	
	private long currentTick;
	private long currentSubTick;
	
	public MeterLogsManager(MeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
	
	public void clearLogs() {
		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().clear();
		}
	}
	
	public void tick() {
		currentTick++;
		currentSubTick = 0;
		
		if (currentTick % 100 == 0) {
			removeOldLogs();
		}
	}
	
	public void syncTime(long currentTick) {
		this.currentTick = currentTick;
		this.currentSubTick = 0;
	}
	
	/**
	 * Remove logs older than the maximum allowed age (10000 ticks by default)
	 */
	private void removeOldLogs() {
		long lastAllowedTick = currentTick - MAX_LOG_AGE;
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().removeOldLogs(lastAllowedTick);
		}
	}
	
	public void poweredChanged(Meter meter, boolean powered) {
		PoweredChangedLog log = new PoweredChangedLog(currentTick, currentSubTick++, powered);
		meter.getLogs().push(log);
	}
	
	public void activeChanged(Meter meter, boolean active) {
		ActiveChangedLog log = new ActiveChangedLog(currentTick, currentSubTick++, active);
		meter.getLogs().push(log);
	}
	
	public void blockMoved(Meter meter, Direction dir) {
		BlockMovedLog log = new BlockMovedLog(currentTick, currentSubTick++, dir);
		meter.getLogs().push(log);
	}
	
	public PacketByteBuf collectLogsData() {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		
		data.writeInt(meterGroup.getMeterCount());
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().encode(data);
		}
		
		return data;
	}
	
	public void updateMeterLogsFromData(PacketByteBuf data) {
		int meterCount = data.readInt();
		
		for (int index = 0; index < meterCount; index++) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter != null) {
				meter.getLogs().decode(data);
			}
		}
	}
	
	public void print() {
		System.out.println("=========================================");
		System.out.println("=========================================");
		
		System.out.println("LOGS");
		
		for (Meter meter : meterGroup.getMeters()) {
			if (meter.getLogs().isEmpty()) {
				continue;
			}
			
			System.out.println("-- METER: " + meter.getName());
			
			meter.getLogs().print();
		}
	}
}
