package redstone.multimeter.common.meter.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

public class MeterEvent {
	
	private EventType type;
	private int metadata;
	
	private MeterEvent() {
		
	}
	
	public MeterEvent(EventType type, int metadata) {
		this.type = type;
		this.metadata = metadata;
	}
	
	@Override
	public String toString() {
		String string = type.getName();
		
		List<Text> lines = new ArrayList<>();
		type.addTextForTooltip(lines, metadata);
		
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
	
	public int getMetadata() {
		return metadata;
	}
	
	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();
		
		nbt.put("type", type.toNbt());
		nbt.putInt("metadata", metadata);
		
		return nbt;
	}
	
	public static MeterEvent fromNbt(CompoundTag nbt) {
		MeterEvent event = new MeterEvent();
		
		event.type = EventType.fromNbt(nbt.get("type"));
		event.metadata = nbt.getInt("metadata");
		
		return event;
	}
}
