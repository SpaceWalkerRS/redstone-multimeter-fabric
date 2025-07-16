package redstone.multimeter.common.meter.event;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Direction;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.TooltipBuilder;
import redstone.multimeter.util.NbtUtils;

public enum EventType {

	UNKNOWN(-1, "unknown", "unknown"),
	POWERED(0, "powered", "powered") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("became powered", metadata == 1));
		}
	},
	ACTIVE(1, "active", "active") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("became active", metadata == 1));
		}
	},
	MOVED(2, "moved", "moved") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("direction", Direction.byId(metadata).getName()));
		}
	},
	POWER_CHANGE(3, "powerChange", "power_change") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			int oldPower = (metadata >> 8) & 0xFF;
			int newPower =  metadata       & 0xFF;

			builder
				.line(Texts.keyValue("old power", oldPower))
				.line(Texts.keyValue("new power", newPower));
		}
	},
	RANDOM_TICK(4, "randomTick", "random_tick"),
	SCHEDULED_TICK(5, "scheduledTick", "scheduled_tick") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			String status = ((metadata >> 30) == 1) ? "scheduling" : "performing";
			int priority = (metadata & 0xF) - 3;

			builder
				.line(Texts.keyValue("status", status))
				.line(Texts.keyValue("priority", priority));
		}
	},
	BLOCK_EVENT(6, "blockEvent", "block_event") {

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
	ENTITY_TICK(7, "entityTick", "entity_tick"),
	BLOCK_ENTITY_TICK(8, "blockEntityTick", "block_entity_tick"),
	BLOCK_UPDATE(9, "blockUpdate", "block_update"),
	COMPARATOR_UPDATE(10, "comparatorUpdate", "comparator_update"),
	SHAPE_UPDATE(11, "shapeUpdate", "shape_update") {

		@Override
		public void buildTooltip(TooltipBuilder builder, int metadata) {
			builder.line(Texts.keyValue("direction", Direction.byId(metadata).getName()));
		}
	},
	OBSERVER_UPDATE(12, "observerUpdate", "observer_update"),
	INTERACT_BLOCK(13, "interactBlock", "interact_block");

	public static final EventType[] ALL;
	private static final Map<String, EventType> BY_LEGACY_KEY;

	static {

		EventType[] types = values();

		ALL = new EventType[types.length - 1];
		BY_LEGACY_KEY = new HashMap<>();

		for (int index = 1; index < types.length; index++) {
			EventType type = types[index];

			ALL[type.id] = type;
			BY_LEGACY_KEY.put(type.legacyKey, type);
		}
	}

	private final int id;
	private final String key;
	// used for backwards compatibility
	// int networking and keybinds
	private final String legacyKey;

	private EventType(int id, String key, String legacyKey) {
		this.id = id;
		this.key = key;
		this.legacyKey = legacyKey;
	}

	public int getId() {
		return this.id;
	}

	public static EventType byId(int id) {
		if (id >= 0 && id < ALL.length) {
			return ALL[id];
		}

		return UNKNOWN;
	}

	public String getKey() {
		return this.key;
	}

	public String getLegacyKey() {
		return this.legacyKey;
	}

	public static EventType byLegacyKey(String name) {
		return BY_LEGACY_KEY.getOrDefault(name, UNKNOWN);
	}

	public Text getName() {
		return Texts.translatable("rsmm.meterControls.eventType." + this.key);
	}

	public int flag() {
		return 1 << this.id;
	}

	public void buildTooltip(TooltipBuilder builder, int metadata) {
	}

	public NbtElement toNbt() {
		return new NbtByte((byte)this.id);
	}

	public static EventType fromNbt(NbtElement nbt) {
		if (nbt.getType() != NbtUtils.TYPE_BYTE) {
			return UNKNOWN;
		}

		NbtByte nbtByte = (NbtByte)nbt;
		int index = nbtByte.getByte();

		return byId(index);
	}
}
