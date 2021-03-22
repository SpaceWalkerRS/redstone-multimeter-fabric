package rsmm.fabric.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import rsmm.fabric.util.PacketUtils;

public class Meter {
	
	//private final MeterLogs logs;
	
	private WorldPos pos;
	private String name;
	private int color;
	private boolean movable;
	
	private boolean powered; // true if the block is receiving power
	private boolean active;  // true if the block is emitting power or active in another way
	
	private boolean dirty;
	
	public Meter(WorldPos pos, String name, int color, boolean movable, boolean initialPowered, boolean initialActive) {
		this.pos = pos;
		this.name = name;
		this.color = color;
		this.movable = movable;
		
		this.powered = initialPowered;
		this.active = initialActive;
	}
	
	/**
	 * Creates an empty Meter, to be populated by packet data
	 */
	public Meter() {
		
	}
	
	public WorldPos getPos() {
		return pos;
	}
	
	public void setPos(WorldPos pos) {
		this.pos = pos;
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
	
	public boolean isPowered() {
		return powered;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public boolean blockUpdate(boolean powered) {
		if (this.powered != powered) {
			this.powered = powered;
			dirty = true;
			
			return true;
		}
		
		return false;
	}
	
	public boolean stateChanged(boolean active) {
		if (this.active != active) {
			this.active = active;
			dirty = true;
			
			return true;
		}
		
		return false;
	}
	
	public boolean blockMoved(Direction dir) {
		if (movable) {
			pos = pos.offset(dir);
			dirty = true;
			
			return true;
		}
		
		return false;
	}
	
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
		buffer.writeString(name);
		buffer.writeInt(color);
		buffer.writeBoolean(movable);
		
		buffer.writeBoolean(powered);
		buffer.writeBoolean(active);
	}
	
	public void decode(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		color = buffer.readInt();
		movable = buffer.readBoolean();
		
		powered = buffer.readBoolean();
		active = buffer.readBoolean();
	}
	
	public void writeLogs(PacketByteBuf buffer) {
		dirty = false;
		
		buffer.writeBoolean(powered);
		buffer.writeBoolean(active);
	}
	
	public void readLogs(PacketByteBuf buffer) {
		powered = buffer.readBoolean();
		active = buffer.readBoolean();
	}
}
