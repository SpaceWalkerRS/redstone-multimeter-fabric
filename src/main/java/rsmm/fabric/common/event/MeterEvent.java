package rsmm.fabric.common.event;

import net.minecraft.nbt.CompoundTag;

import rsmm.fabric.util.NBTUtils;

public class MeterEvent {
	
	private EventType type;
	private long tick;
	private int subTick;
	private int metaData;
	
	private MeterEvent() {
		
	}
	
	public MeterEvent(EventType type, long tick, int subTick, int metaData) {
		this.type = type;
		
		this.tick = tick;
		this.subTick = subTick;
		this.metaData = metaData;
	}
	
	public EventType getType() {
		return type;
	}
	
	public long getTick() {
		return tick;
	}
	
	public int getSubTick() {
		return subTick;
	}
	
	public boolean isAt(long tick) {
		return this.tick == tick;
	}
	
	public boolean isAt(long tick, int subTick) {
		return this.tick == tick && this.subTick == subTick;
	}
	
	public boolean isBefore(long tick) {
		return this.tick < tick;
	}
	
	public boolean isBefore(long tick, int subTick) {
		if (this.tick == tick) {
			return this.subTick < subTick;
		}
		
		return this.tick < tick;
	}
	
	public boolean isAfter(long tick) {
		return this.tick > tick;
	}
	
	public boolean isAfter(long tick, int subTick) {
		if (this.tick == tick) {
			return this.subTick > subTick;
		}
		
		return this.tick > tick;
	}
	
	public int getMetaData() {
		return metaData;
	}
	
	public CompoundTag toTag() {
		CompoundTag data = new CompoundTag();
		
		NBTUtils.putEventType(data, "type", type);
		data.putLong("tick", tick);
		data.putInt("subTick", subTick);
		data.putInt("metaData", metaData);
		
		return data;
	}
	
	public void fromTag(CompoundTag data) {
		type = NBTUtils.getEventType(data, "type");
		tick = data.getLong("tick");
		subTick = data.getInt("subTick");
		metaData = data.getInt("metaData");
	}
	
	public static MeterEvent createFromTag(CompoundTag tag) {
		MeterEvent event = new MeterEvent();
		event.fromTag(tag);
		
		return event;
	}
}
