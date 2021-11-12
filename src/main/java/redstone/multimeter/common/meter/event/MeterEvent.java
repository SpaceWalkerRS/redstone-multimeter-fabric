package redstone.multimeter.common.meter.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

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
	
	public NbtCompound toNBT() {
		NbtCompound nbt = new NbtCompound();
		
		nbt.put("type", type.toNBT());
		nbt.putInt("metaData", metaData);
		
		return nbt;
	}
	
	public static MeterEvent fromNBT(NbtCompound nbt) {
		MeterEvent event = new MeterEvent();
		
		event.type = EventType.fromNBT(nbt.get("type"));
		event.metaData = nbt.getInt("metaData");
		
		return event;
	}
}
