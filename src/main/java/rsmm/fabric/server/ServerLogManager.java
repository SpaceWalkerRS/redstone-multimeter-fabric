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
	
	public void resetSubTickCount() {
		currentSubTick = 0;
	}
	
	public void logEvent(Meter meter, EventType type, int metaData) {
		if (meter.isMetering(type)) {
			MeterEvent event = new MeterEvent(type, getCurrentTick(), currentSubTick++, metaData);
			meter.getLogs().add(event);
		}
		
		meterGroup.markDirty();
		meter.markDirty();
	}
	
	public PacketByteBuf collectMeterData() {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		
		data.writeInt(currentSubTick);
		
		for (int index = 0; index < meterGroup.getMeterCount(); index++) {
			Meter meter = meterGroup.getMeter(index);
			
			if (meter.isDirty()) {
				data.writeBoolean(true);
				data.writeInt(index);
				
				meter.writeData(data);
			}
		}
		
		data.writeBoolean(false); // mark that there is no further data
		
		return data;
	}
}
