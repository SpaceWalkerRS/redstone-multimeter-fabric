package redstone.multimeter.common.meter.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

import redstone.multimeter.util.NBTUtils;

public class MeterEvent {
	
	private EventType type;
	private int metaData;
	
	private MeterEvent() {
		
	}
	
	public MeterEvent(EventType type, int metaData) {
		this.type = type;
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
	
	public int getMetaData() {
		return metaData;
	}
	
	public CompoundTag toNBT() {
		CompoundTag nbt = new CompoundTag();
		
		NBTUtils.putEventType(nbt, "type", type);
		nbt.putInt("metaData", metaData);
		
		return nbt;
	}
	
	public static MeterEvent fromNBT(CompoundTag nbt) {
		MeterEvent event = new MeterEvent();
		
		event.type = NBTUtils.getEventType(nbt, "type");
		event.metaData = nbt.getInt("metaData");
		
		return event;
	}
}
