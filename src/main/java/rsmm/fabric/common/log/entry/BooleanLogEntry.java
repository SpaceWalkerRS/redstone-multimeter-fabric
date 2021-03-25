package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;

public class BooleanLogEntry extends LogEntry<Boolean> {
	
	public BooleanLogEntry(LogType<BooleanLogEntry> type) {
		super(type);
	}
	
	public BooleanLogEntry(LogType<BooleanLogEntry> type, long tick, long subTick, Boolean value) {
		super(type, tick, subTick, value);
	}
	
	@Override
	protected void writeValue(PacketByteBuf buffer) {
		buffer.writeBoolean(value);
	}
	
	@Override
	protected void readValue(PacketByteBuf buffer) {
		value = buffer.readBoolean();
	}
}
