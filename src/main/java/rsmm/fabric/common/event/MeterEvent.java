package rsmm.fabric.common.event;

import net.minecraft.util.PacketByteBuf;

public class MeterEvent {
	
	private EventType type;
	private long tick;
	private int subTick;
	private int metaData;
	
	public MeterEvent() {
		
	}
	
	public MeterEvent(EventType type, long tick, int subTick, int metaData) {
		this.type = type;
		
		this.tick = tick;
		this.subTick = subTick;
		this.metaData = metaData;
	}
	
	public EventType getType() {
		return type;
	}
	
	public long getTick() {
		return tick;
	}
	
	public int getSubTick() {
		return subTick;
	}
	
	public boolean isAt(long tick) {
		return this.tick == tick;
	}
	
	public boolean isAt(long tick, int subTick) {
		return this.tick == tick && this.subTick == subTick;
	}
	
	public boolean isBefore(long tick) {
		return this.tick < tick;
	}
	
	public boolean isBefore(long tick, int subTick) {
		if (this.tick == tick) {
			return this.subTick < subTick;
		}
		
		return this.tick < tick;
	}
	
	public boolean isAfter(long tick) {
		return this.tick > tick;
	}
	
	public boolean isAfter(long tick, int subTick) {
		if (this.tick == tick) {
			return this.subTick > subTick;
		}
		
		return this.tick > tick;
	}
	
	public int getMetaData() {
		return metaData;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(type.getIndex());
		buffer.writeLong(tick);
		buffer.writeInt(subTick);
		buffer.writeInt(metaData);
	}
	
	public void decode(PacketByteBuf buffer) {
		type = EventType.fromIndex(buffer.readByte());
		tick = buffer.readLong();
		subTick = buffer.readInt();
		metaData = buffer.readInt();
	}
}
