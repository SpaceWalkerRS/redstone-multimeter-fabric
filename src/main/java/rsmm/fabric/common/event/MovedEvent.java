package rsmm.fabric.common.event;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

public class MovedEvent extends MeterEvent {
	
	private Direction dir;
	
	public MovedEvent() {
		super(EventType.MOVED);
	}
	
	public MovedEvent(long tick, long subTick, Direction dir) {
		super(EventType.MOVED, tick, subTick);
		
		this.dir = dir;
	}
	
	@Override
	protected void encodeEvent(PacketByteBuf buffer) {
		buffer.writeByte(dir.getId());
	}
	
	@Override
	protected void decodeEvent(PacketByteBuf buffer) {
		dir = Direction.byId(buffer.readByte());
	}
	
	public Direction getDir() {
		return dir;
	}
}
