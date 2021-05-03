package rsmm.fabric.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.listeners.MeterChangeDispatcher;
import rsmm.fabric.common.log.MeterLogs;
import rsmm.fabric.util.NBTUtils;

public class Meter {
	
	private final MeterLogs logs;
	
	private WorldPos pos;
	private String name;
	private int color;
	private boolean movable;
	
	/** The event types being metered */
	private int eventTypes;
	/** true if the block at this position is receiving power */
	private boolean powered;
	/** true if the block at this position is emitting power or active in another way */
	private boolean active;
	
	/** This property is used on the server to mark this meter as having changes that need to be synced with clients */
	private boolean dirty;
	
	public Meter(WorldPos pos, String name, int color, boolean movable, int initialEventTypes, boolean initialPowered, boolean initialActive) {
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
	private Meter() {
		this.logs = new MeterLogs();
	}
	
	public MeterLogs getLogs() {
		return logs;
	}
	
	public void clearLogs() {
		logs.clear();
	}
	
	public WorldPos getPos() {
		return pos;
	}
	
	public boolean isIn(World world) {
		return pos.isOf(world);
	}
	
	public void setPos(WorldPos pos) {
		if (this.pos == null || !this.pos.equals(pos)) {
			this.pos = pos;
			MeterChangeDispatcher.posChanged(this);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (this.name == null || !this.name.equals(name)) {
			this.name = name;
			MeterChangeDispatcher.nameChanged(this);
		}
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		if (this.color != color) {
			this.color = color;
			MeterChangeDispatcher.colorChanged(this);
		}
	}
	
	public boolean isMovable() {
		return movable;
	}
	
	public void setIsMovable(boolean movable) {
		if (this.movable != movable) {
			this.movable = movable;
			MeterChangeDispatcher.isMovableChanged(this);
		}
	}
	
	public int getMeteredEventTypes() {
		return eventTypes;
	}
	
	public boolean isMetering(EventType type) {
		return (eventTypes & type.flag()) != 0;
	}
	
	public void startMetering(EventType type) {
		setMeteredEventTypes(eventTypes | type.flag());
	}
	
	public void stopMetering(EventType type) {
		setMeteredEventTypes(eventTypes & ~type.flag());
	}
	
	public void setMeteredEventTypes(int eventTypes) {
		if (this.eventTypes != eventTypes) {
			this.eventTypes = eventTypes;
			MeterChangeDispatcher.meteredEventsChanged(this);
		}
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
	
	/**
	 * Check if this meter has changes that need to be synced with clients
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * Mark this meter as having changes that need to be synced with clients
	 */
	public void markDirty() {
		dirty = true;
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
			setPos(pos.offset(dir));
			
			return true;
		}
		
		return false;
	}
	
	public void cleanUp() {
		dirty = false;
		logs.clear();
	}
	
	public CompoundTag toTag() {
		return toTag(new CompoundTag());
	}
	
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("pos", NBTUtils.worldPosToTag(pos));
		tag.putString("name", name);
		tag.putInt("color", color);
		tag.putBoolean("movable", movable);
		
		tag.putInt("eventTypes", eventTypes);
		tag.putBoolean("powered", powered);
		tag.putBoolean("active", active);
		
		return tag;
	}
	
	public Meter fromTag(CompoundTag tag) {
		setPos(NBTUtils.tagToWorldPos(tag.getCompound("pos")));
		setName(tag.getString("name"));
		setColor(tag.getInt("color"));
		setIsMovable(tag.getBoolean("movable"));
		
		setMeteredEventTypes(tag.getInt("eventTypes"));
		powered = tag.getBoolean("powered");
		active = tag.getBoolean("active");
		
		return this;
	}
	
	public static Meter createFromTag(CompoundTag tag) {
		return new Meter().fromTag(tag);
	}
	
	public CompoundTag collectData() {
		CompoundTag data = toTag();
		data.put("logs", logs.toTag());
		
		return data;
	}
	
	public void updateFromData(CompoundTag data) {
		fromTag(data);
		logs.updateFromTag(data.getCompound("logs"));
	}
}
