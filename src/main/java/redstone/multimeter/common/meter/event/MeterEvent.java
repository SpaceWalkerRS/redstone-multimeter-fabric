package redstone.multimeter.common.meter.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;
import redstone.multimeter.client.gui.tooltip.Tooltip;
import redstone.multimeter.client.gui.tooltip.TooltipBuilder;
import redstone.multimeter.client.gui.tooltip.Tooltips;

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
		String string = type.getLegacyKey();

		Tooltip tooltip = Tooltips.builder()
			.lines(this::buildTooltip)
			.build();

		List<String> parts = new ArrayList<>();
		int partCount = 0;

		for (Text line : tooltip) {
			// first line is event type name
			if (partCount++ > 0) {
				parts.add(line.buildString());
			}
		}

		if (!parts.isEmpty()) {
			string += "[" + String.join(", ", parts) + "]";
		}

		return string;
	}

	public EventType getType() {
		return type;
	}

	public int getMetadata() {
		return metadata;
	}

	public void buildTooltip(TooltipBuilder builder) {
		builder
			.line(Texts.keyValue("event type", type.getName()))
			.lines(b -> type.buildTooltip(builder, metadata));
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
		event.metadata = nbt.getInt("metadata").get();

		return event;
	}
}
