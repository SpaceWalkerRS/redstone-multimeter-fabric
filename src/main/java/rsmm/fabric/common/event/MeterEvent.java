package rsmm.fabric.common.event;

import net.minecraft.network.PacketByteBuf;

public abstract class MeterEvent {
	
	private final EventType<? extends MeterEvent> type;
	
	private long tick;
	private long subTick;
	
	protected MeterEvent(EventType<? extends MeterEvent> type) {
		this.type = type;
	}
	
	protected MeterEvent(EventType<? extends MeterEvent> type, long tick, long subTick) {
		this.type = type;
		
		this.tick = tick;
		this.subTick = subTick;
	}
	
	public EventType<?> getType() {
		return type;
	}
	
	public long getTick() {
		return tick;
	}
	
	public long getSubTick() {
		return subTick;
	}
	
	public boolean isBefore(long tick, long subTick) {
		if (this.tick == tick) {
			return this.subTick < subTick;
		}
		
		return this.tick < tick;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeLong(tick);
		buffer.writeLong(subTick);
		
		encodeEvent(buffer);
	}
	
	protected abstract void encodeEvent(PacketByteBuf buffer);
	
	public void decode(PacketByteBuf buffer) {
		tick = buffer.readLong();
		subTick = buffer.readLong();
		
		decodeEvent(buffer);
	}
	
	protected abstract void decodeEvent(PacketByteBuf buffer);
	
}
