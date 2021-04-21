package rsmm.fabric.common;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.log.MeterLogs;
import rsmm.fabric.util.PacketUtils;

public class Meter {
	
	private final MeterLogs logs;
	
	private DimPos pos;
	private String name;
	private int color;
	private boolean movable;
	
	/** The event types being metered */
	private int eventTypes;
	/** true if the block at this position is receiving power */
	private boolean powered;
	/** true if the block at this position is emitting power or active in another way */
	private boolean active;
	
	public Meter(DimPos pos, String name, int color, boolean movable, int initialEventTypes, boolean initialPowered, boolean initialActive) {
		this.logs = new MeterLogs();
		
		this.pos = pos;
		this.name = name;
		this.color = color;
		this.movable = movable;
		
		this.eventTypes = initialEventTypes;
		this.powered = initialPowered;
		this.active = initialActive;
	}
	
	/**
	 * Creates an empty Meter, to be populated by packet data
	 */
	public Meter() {
		this.logs = new MeterLogs();
	}
	
	public MeterLogs getLogs() {
		return logs;
	}
	
	public void clearLogs() {
		logs.clear();
	}
	
	public DimPos getPos() {
		return pos;
	}
	
	public boolean isIn(World world) {
		return pos.isOf(world);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public boolean isMovable() {
		return movable;
	}
	
	public boolean isMetering(EventType type) {
		return (eventTypes & type.flag()) != 0;
	}
	
	public void startMetering(EventType type) {
		eventTypes |= type.flag();
	}
	
	public void stopMetering(EventType type) {
		eventTypes &= ~type.flag();
	}
	
	/**
	 * return true if the block at this position is receiving power
	 */
	public boolean isPowered() {
		return powered;
	}
	
	/**
	 * return true if the block at this position is emitting power
	 * or active in another way
	 */
	public boolean isActive() {
		return active;
	}
	
	public boolean blockUpdate(boolean powered) {
		if (this.powered != powered) {
			this.powered = powered;
			
			return true;
		}
		
		return false;
	}
	
	public boolean stateChanged(boolean active) {
		if (this.active != active) {
			this.active = active;
			
			return true;
		}
		
		return false;
	}
	
	public boolean blockMoved(Direction dir) {
		if (movable) {
			pos = pos.offset(dir);
			
			return true;
		}
		
		return false;
	}
	
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
		buffer.writeString(name);
		buffer.writeInt(color);
		buffer.writeBoolean(movable);
		
		buffer.writeInt(eventTypes);
		buffer.writeBoolean(powered);
		buffer.writeBoolean(active);
	}
	
	public void decode(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		color = buffer.readInt();
		movable = buffer.readBoolean();
		
		eventTypes = buffer.readInt();
		powered = buffer.readBoolean();
		active = buffer.readBoolean();
	}
	
	public void writeLogs(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
		buffer.writeBoolean(powered);
		buffer.writeBoolean(active);
		
		logs.encode(buffer);
	}
	
	public void readLogs(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
		powered = buffer.readBoolean();
		active = buffer.readBoolean();
		
		logs.decode(buffer);
	}
}
