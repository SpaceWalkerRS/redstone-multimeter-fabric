package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

public class DirectionLogEntry extends LogEntry<Direction> {
	
	public DirectionLogEntry(LogType<DirectionLogEntry> type) {
		super(type);
	}
	
	public DirectionLogEntry(LogType<DirectionLogEntry> type, long tick, long subTick, Direction value) {
		super(type, tick, subTick, value);
	}
	
	@Override
	protected void writeValue(PacketByteBuf buffer) {
		buffer.writeByte(value.getId());
	}
	
	@Override
	protected void readValue(PacketByteBuf buffer) {
		value = Direction.byId(buffer.readByte());
	}
}
