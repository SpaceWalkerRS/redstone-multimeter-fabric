package rsmm.fabric.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.event.MeterEvent;
import rsmm.fabric.common.log.LogManager;

public class ServerLogManager extends LogManager {
	
	private final ServerMeterGroup meterGroup;
	
	private int currentSubTick;
	
	public ServerLogManager(ServerMeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
	
	@Override
	protected MeterGroup getMeterGroup() {
		return meterGroup;
	}
	
	@Override
	protected long getCurrentTick() {
		return meterGroup.getMultimeter().getMultimeterServer().getMinecraftServer().getTicks();
	}
	
	public boolean hasLogs() {
		return currentSubTick > 0;
	}
	
	public void resetSubTickCount() {
		currentSubTick = 0;
	}
	
	public void logEvent(Meter meter, EventType type, int metaData) {
		meter.getLogs().add(new MeterEvent(type, getCurrentTick(), currentSubTick++, metaData));
	}
	
	public PacketByteBuf collectMeterLogs() {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		
		data.writeInt(currentSubTick);
		
		for (Meter meter : meterGroup.getMeters()) {
			meter.writeLogs(data);
		}
		
		return data;
	}
}
