package redstone.multimeter.common.meter.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import redstone.multimeter.util.TextUtils;

public enum EventType {
	
	POWERED(0, "powered") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			TextUtils.addFancyText(lines, "became powered", metaData == 1);
		}
	},
	ACTIVE(1, "active") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			TextUtils.addFancyText(lines, "became active", metaData == 1);
		}
	},
	MOVED(2, "moved") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			TextUtils.addFancyText(lines, "direction", Direction.byId(metaData).getName());
		}
	},
	POWER_CHANGE(3, "power_change") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			int oldPower = (metaData >> 8) & 0xFF;
			int newPower =  metaData       & 0xFF;
			
			TextUtils.addFancyText(lines, "old power", oldPower);
			TextUtils.addFancyText(lines, "new power", newPower);
		}
	},
	RANDOM_TICK(4, "random_tick") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			
		}
	},
	SCHEDULED_TICK(5, "scheduled_tick") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			TextUtils.addFancyText(lines, "priority", metaData);
		}
	},
	BLOCK_EVENT(6, "block_event") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			TextUtils.addFancyText(lines, "type", metaData);
		}
	},
	ENTITY_TICK(7, "entity_tick") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			
		}
	},
	BLOCK_ENTITY_TICK(8, "block_entity_tick") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			
		}
	},
	BLOCK_UPDATE(9, "block_update") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			
		}
	},
	COMPARATOR_UPDATE(10, "comparator_update") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			
		}
	},
	SHAPE_UPDATE(11, "shape_update") {
		
		@Override
		public void addTextForTooltip(List<Text> lines, int metaData) {
			TextUtils.addFancyText(lines, "direction", Direction.byId(metaData).getName());
		}
	};
	
	public static final EventType[] ALL;
	private static final Map<String, EventType> BY_NAME;
	
	static {
		
		ALL = new EventType[values().length];
		BY_NAME = new HashMap<>();
		
		for (EventType type : values()) {
			ALL[type.index] = type;
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
		if (index >= 0 && index < ALL.length) {
			return ALL[index];
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
	
	public abstract void addTextForTooltip(List<Text> lines, int metaData);
	
}
