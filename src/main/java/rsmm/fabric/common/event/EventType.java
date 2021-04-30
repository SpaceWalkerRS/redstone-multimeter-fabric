package rsmm.fabric.common.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

public enum EventType {
	
	POWERED(0, "powered") {
		
		@Override
		public void addTextForTooltip(List<Text> text, int metaData) {
			boolean powered = (metaData == 1);
			text.add(new LiteralText("became powered: ").formatted(Formatting.GOLD).append(String.valueOf(powered)));
		}
	},
	ACTIVE(1, "active") {
		
		@Override
		public void addTextForTooltip(List<Text> text, int metaData) {
			boolean active = (metaData == 1);
			text.add(new LiteralText("became active: ").formatted(Formatting.GOLD).append(String.valueOf(active)));
		}
	},
	MOVED(2, "moved") {
		
		@Override
		public void addTextForTooltip(List<Text> text, int metaData) {
			Direction dir = Direction.byId(metaData);
			text.add(new LiteralText("direction: ").formatted(Formatting.GOLD).append(dir.getName()));
		}
	};
	
	public static final EventType[] TYPES;
	private static final Map<String, EventType> BY_NAME;
	
	static {
		TYPES = new EventType[values().length];
		BY_NAME = new HashMap<>();
		
		for (EventType type : values()) {
			TYPES[type.index] = type;
			BY_NAME.put(type.name, type);
		}
	}
	
	private final int index;
	private final String name;
	
	private EventType(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static EventType fromIndex(int index) {
		if (index >= 0 && index < TYPES.length) {
			return TYPES[index];
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public static EventType fromName(String name) {
		return BY_NAME.get(name);
	}
	
	public int flag() {
		return 1 << index;
	}
	
	public abstract void addTextForTooltip(List<Text> text, int metaData);
	
}
