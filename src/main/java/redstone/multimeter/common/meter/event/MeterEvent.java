package redstone.multimeter.common.meter.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import redstone.multimeter.common.TickPhase;
import redstone.multimeter.util.NBTUtils;

public class MeterEvent {
	
	private EventType type;
	private long tick;
	private int subtick;
	private TickPhase tickPhase;
	private int metaData;
	
	private MeterEvent() {
		
	}
	
	public MeterEvent(EventType type, long tick, int subTick, TickPhase tickPhase, int metaData) {
		this.type = type;
		
		this.tick = tick;
		this.subtick = subTick;
		this.tickPhase = tickPhase;
		this.metaData = metaData;
	}
	
	@Override
	public String toString() {
		String string = type.getName();
		
		List<Text> lines = new ArrayList<>();
		type.addTextForTooltip(lines, metaData);
		
		if (!lines.isEmpty()) {
			String[] args = new String[lines.size()];
			
			for (int index = 0; index < lines.size(); index++) {
				args[index] = lines.get(index).getString();
			}
			
			string += "[" + String.join(", ", args) + "]";
		}
		
		return string;
	}
	
	public EventType getType() {
		return type;
	}
	
	public long getTick() {
		return tick;
	}
	
	public int getSubtick() {
		return subtick;
	}
	
	public boolean isAt(long tick) {
		return this.tick == tick;
	}
	
	public boolean isAt(long tick, int subtick) {
		return this.tick == tick && this.subtick == subtick;
	}
	
	public boolean isBefore(long tick) {
		return this.tick < tick;
	}
	
	public boolean isBefore(long tick, int subtick) {
		if (this.tick == tick) {
			return this.subtick < subtick;
		}
		
		return this.tick < tick;
	}
	
	public boolean isBefore(MeterEvent event) {
		return isBefore(event.getTick(), event.getSubtick());
	}
	
	public boolean isAfter(long tick) {
		return this.tick > tick;
	}
	
	public boolean isAfter(long tick, int subtick) {
		if (this.tick == tick) {
			return this.subtick > subtick;
		}
		
		return this.tick > tick;
	}
	
	public boolean isAfter(MeterEvent event) {
		return isAfter(event.getTick(), event.getSubtick());
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
		addTextForTooltip(lines, "subtick", subtick);
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
	
	public NbtCompound toNBT() {
		NbtCompound nbt = new NbtCompound();
		
		NBTUtils.putEventType(nbt, "type", type);
		nbt.putLong("tick", tick);
		nbt.putInt("subtick", subtick);
		NBTUtils.putTickPhase(nbt, "tickPhase", tickPhase);
		nbt.putInt("metaData", metaData);
		
		return nbt;
	}
	
	public static MeterEvent fromNBT(NbtCompound nbt) {
		MeterEvent event = new MeterEvent();
		
		event.type = NBTUtils.getEventType(nbt, "type");
		event.tick = nbt.getLong("tick");
		event.subtick = nbt.getInt("subtick");
		event.tickPhase = NBTUtils.getTickPhase(nbt, "tickPhase");
		event.metaData = nbt.getInt("metaData");
		
		return event;
	}
}
