package redstone.multimeter.common.meter.event;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import redstone.multimeter.client.gui.Tooltip;

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
		
		Tooltip tooltip = new Tooltip();
		type.addTextToTooltip(tooltip, metadata);
		
		if (!tooltip.isEmpty()) {
			List<ITextComponent> lines = tooltip.getLines();
			String[] args = new String[lines.size()];
			
			for (int index = 0; index < lines.size(); index++) {
				args[index] = lines.get(index).getUnformattedText();
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
	
	public NBTTagCompound toNbt() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setTag("type", type.toNbt());
		nbt.setInteger("metadata", metadata);
		
		return nbt;
	}
	
	public static MeterEvent fromNbt(NBTTagCompound nbt) {
		MeterEvent event = new MeterEvent();
		
		event.type = EventType.fromNbt(nbt.getTag("type"));
		event.metadata = nbt.getInteger("metadata");
		
		return event;
	}
}
