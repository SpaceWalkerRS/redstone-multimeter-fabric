package rsmm.fabric.common.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import rsmm.fabric.common.TickPhase;
import rsmm.fabric.util.NBTUtils;

public class MeterEvent {
	
	private EventType type;
	private long tick;
	private int subTick;
	private TickPhase tickPhase;
	private int metaData;
	
	private MeterEvent() {
		
	}
	
	public MeterEvent(EventType type, long tick, int subTick, TickPhase tickPhase, int metaData) {
		this.type = type;
		
		this.tick = tick;
		this.subTick = subTick;
		this.tickPhase = tickPhase;
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
	
	public boolean isBefore(MeterEvent event) {
		return isBefore(event.getTick(), event.getSubTick());
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
	
	public boolean isAfter(MeterEvent event) {
		return isAfter(event.getTick(), event.getSubTick());
	}
	
	public TickPhase getTickPhase() {
		return tickPhase;
	}
	
	public int getMetaData() {
		return metaData;
	}
	
	public List<Text> getTextForTooltip() {
		List<Text> lines = new ArrayList<>();
		
		addTextForTooltip(lines, "event type", type.getName());
		type.addTextForTooltip(lines, metaData);
		addTextForTooltip(lines, "tick", tick);
		addTextForTooltip(lines, "subtick", subTick);
		addTextForTooltip(lines, "tick phase", tickPhase.getName());
		
		return lines;
	}
	
	public static void addTextForTooltip(List<Text> lines, String title, Object info) {
		addTextForTooltip(lines, title, info.toString());
	}
	
	public static void addTextForTooltip(List<Text> lines, String title, String info) {
		lines.add(formatTextForTooltip(title, info));
	}
	
	public static Text formatTextForTooltip(String title, String info) {
		return new LiteralText("").
			append(new LiteralText(title + ": ").formatted(Formatting.GOLD)).
			append(new LiteralText(info));
	}
	
	public CompoundTag toTag() {
		CompoundTag data = new CompoundTag();
		
		NBTUtils.putEventType(data, "type", type);
		data.putLong("tick", tick);
		data.putInt("subTick", subTick);
		NBTUtils.putTickPhase(data, "tickPhase", tickPhase);
		data.putInt("metaData", metaData);
		
		return data;
	}
	
	public void fromTag(CompoundTag data) {
		type = NBTUtils.getEventType(data, "type");
		tick = data.getLong("tick");
		subTick = data.getInt("subTick");
		tickPhase = NBTUtils.getTickPhase(data, "tickPhase");
		metaData = data.getInt("metaData");
	}
	
	public static MeterEvent createFromTag(CompoundTag tag) {
		MeterEvent event = new MeterEvent();
		event.fromTag(tag);
		
		return event;
	}
}
