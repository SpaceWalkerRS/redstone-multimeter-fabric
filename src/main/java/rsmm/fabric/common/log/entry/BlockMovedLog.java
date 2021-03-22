package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;
import rsmm.fabric.common.log.LogType;

public class BlockMovedLog extends LogEntry {
	
	private Direction dir;
	
	/**
	 * Creates an empty log to be populated by packet data
	 */
	public BlockMovedLog() {
		super(LogType.BLOCK_MOVED, -1, -1);
	}
	
	public BlockMovedLog(long tick, long subTick, Direction dir) {
		super(LogType.BLOCK_MOVED, tick, subTick);
		this.dir = dir;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		super.encode(buffer);
		buffer.writeByte(dir.getId());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		super.decode(buffer);
		dir = Direction.byId(buffer.readByte());
	}
	
	public Direction getDir() {
		return dir;
	}
	
	@Override
	public void print() {
		System.out.println(getTick() + "t " + getSubTick() + " - block moved " + dir);
	}
}
