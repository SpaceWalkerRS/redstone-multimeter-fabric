package rsmm.fabric.common.event;

import net.minecraft.network.PacketByteBuf;

public abstract class MeterEvent {
	
	private long tick;
	private long subTick;
	
	protected MeterEvent() {
		
	}
	
	protected MeterEvent(long tick, long subTick) {
		this.tick = tick;
		this.subTick = subTick;
	}
	
	public long getTick() {
		return tick;
	}
	
	public long getSubTick() {
		return subTick;
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
