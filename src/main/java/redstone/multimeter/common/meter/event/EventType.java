package redstone.multimeter.common.meter.event;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;

import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.TooltipBuilder;
import redstone.multimeter.util.NbtUtils;

public enum EventType {

	UNKNOWN(-1, "unknown"),
	POWERED(0, "powered") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("became powered", metadata == 1));
		}
	},
	ACTIVE(1, "active") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("became active", metadata == 1));
		}
	},
	MOVED(2, "moved") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("direction", Direction.from3DDataValue(metadata).getName()));
		}
	},
	POWER_CHANGE(3, "power_change") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			int oldPower = (metadata >> 8) & 0xFF;
			int newPower =  metadata       & 0xFF;

			builder
				.line(Texts.keyValue("old power", oldPower))
				.line(Texts.keyValue("new power", newPower));
		}
	},
	RANDOM_TICK(4, "random_tick"),
	SCHEDULED_TICK(5, "scheduled_tick") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			String status = ((metadata >> 30) == 1) ? "scheduling" : "performing";
			int priority = (metadata & 0xF) - 3;

			builder
				.line(Texts.keyValue("status", status))
				.line(Texts.keyValue("priority", priority));
		}
	},
	BLOCK_EVENT(6, "block_event") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			String status = ((metadata >> 30) == 1) ? "queueing" : "performing";
			int depth = (metadata >> 4) & 0xFFFF;
			int type  =  metadata       & 0xF;

			builder
				.line(Texts.keyValue("status", status))
				.line(Texts.keyValue("type", type))
				.line(Texts.keyValue("depth", depth));
		}
	},
	ENTITY_TICK(7, "entity_tick"),
	BLOCK_ENTITY_TICK(8, "block_entity_tick"),
	BLOCK_UPDATE(9, "block_update"),
	COMPARATOR_UPDATE(10, "comparator_update"),
	SHAPE_UPDATE(11, "shape_update") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("direction", Direction.from3DDataValue(metadata).getName()));
		}
	},
	OBSERVER_UPDATE(12, "observer_update"),
	INTERACT_BLOCK(13, "interact_block");

	public static final EventType[] ALL;
	private static final Map<String, EventType> BY_NAME;

	static {

		EventType[] types = values();

		ALL = new EventType[types.length - 1];
		BY_NAME = new HashMap<>();

		for (int index = 1; index < types.length; index++) {
			EventType type = types[index];

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

	public static EventType byIndex(int index) {
		if (index >= 0 && index < ALL.length) {
			return ALL[index];
		}
		
		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

	public static EventType byName(String name) {
		return BY_NAME.getOrDefault(name, UNKNOWN);
	}

	public int flag() {
		return 1 << index;
	}

	public void buildTooltip(TooltipBuilder builder, int metadata) {
	}

	public Tag toNbt() {
		return new ByteTag((byte)index);
	}

	public static EventType fromNbt(Tag nbt) {
		if (nbt.getId() != NbtUtils.TYPE_BYTE) {
			return UNKNOWN;
		}

		ByteTag nbtByte = (ByteTag)nbt;
		int index = nbtByte.getAsByte();

		return byIndex(index);
	}
}
