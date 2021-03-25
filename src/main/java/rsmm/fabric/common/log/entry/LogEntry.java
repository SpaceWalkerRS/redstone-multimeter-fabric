package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;

public abstract class LogEntry<T> {
	
	private final LogType<? extends LogEntry<?>> type;
	
	private long tick;
	private long subTick;
	
	protected T value;
	
	protected LogEntry(LogType<? extends LogEntry<?>> logType) {
		this.type = logType;
	}
	
	protected LogEntry(LogType<? extends LogEntry<?>> logType, long tick, long subTick, T value) {
		this.type = logType;
		
		this.tick = tick;
		this.subTick = subTick;
		
		this.value = value;
	}
	
	public LogType<? extends LogEntry<?>> getType() {
		return type;
	}
	
	public long getTick() {
		return tick;
	}
	
	public long getSubTick() {
		return subTick;
	}
	
	public T get() {
		return value;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeLong(tick);
		buffer.writeLong(subTick);
		
		writeValue(buffer);
	}
	
	protected abstract void writeValue(PacketByteBuf buffer);
	
	public void decode(PacketByteBuf buffer) {
		tick = buffer.readLong();
		subTick = buffer.readLong();
		
		readValue(buffer);
	}
	
	protected abstract void readValue(PacketByteBuf buffer);
}
