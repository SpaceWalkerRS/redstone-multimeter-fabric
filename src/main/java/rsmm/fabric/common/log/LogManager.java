package rsmm.fabric.common.log;

import java.lang.reflect.Constructor;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.log.entry.LogEntry;
import rsmm.fabric.common.log.entry.LogType;

public class LogManager {
	
	private static final int MAX_LOG_AGE = 10000;
	
	private final MeterGroup meterGroup;
	
	private long currentTick;
	private long currentSubTick;
	
	private long cutoff;
	
	public LogManager(MeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
	
	public void tick() {
		currentTick++;
		currentSubTick = 0;
		
		cutoff++;
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.getLogs().clearOldLogs(cutoff);
		}
	}
	
	public void syncTime(long currentTick) {
		this.currentTick = currentTick;
		this.cutoff = this.currentTick - MAX_LOG_AGE;
	}
	
	public void clearLogs() {
		for (Meter meter : meterGroup.getMeters()) {
			meter.clearLogs();
		}
	}
	
	public <T> void log(Meter meter, LogType<? extends LogEntry<T>> type, T value) {
		try {
			Constructor<? extends LogEntry<T>> constructor = type.entry().getDeclaredConstructor(LogType.class, long.class, long.class, value.getClass());
			LogEntry<?> log = constructor.newInstance(type, currentTick, currentSubTick++, value);
			
			meter.getLogs().push(log);
		} catch (Exception e) {
			
		}
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
