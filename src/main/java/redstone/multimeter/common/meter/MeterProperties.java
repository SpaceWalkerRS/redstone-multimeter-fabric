package redstone.multimeter.common.meter;

import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.nbt.NbtCompound;

import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.event.EventType;
import redstone.multimeter.util.ColorUtils;

public class MeterProperties {

	private DimPos pos;
	private String name;
	private Integer color;
	private Boolean movable;
	private Integer eventTypes;

	public MeterProperties() {
	}

	public MeterProperties(DimPos pos, String name, Integer color, Boolean movable, Integer eventTypes) {
		this.pos = pos;
		this.name = name;
		this.color = color;
		this.movable = movable;
		this.eventTypes = eventTypes;
	}

	@Override
	public String toString() {
		return String.format("MeterProperties[pos: %s, name: %s, color: %s, movable: %s, event types: %s]", pos, name, color, movable, eventTypes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pos, name, color, movable, eventTypes);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MeterProperties)) {
			return false;
		}

		MeterProperties p = (MeterProperties)o;

		return Objects.equals(pos, p.pos)
			&& Objects.equals(name, p.name)
			&& Objects.equals(color, p.color)
			&& Objects.equals(movable, p.movable)
			&& Objects.equals(eventTypes, p.eventTypes);
	}

	public DimPos getPos() {
		return pos;
	}

	public String getName() {
		return name;
	}

	public Integer getColor() {
		return color;
	}

	public Boolean getMovable() {
		return movable;
	}

	public Integer getEventTypes() {
		return eventTypes;
	}

	public boolean hasEventType(EventType type) {
		return eventTypes != null && (eventTypes & type.flag()) != 0;
	}

	public MutableMeterProperties mutable() {
		return new MutableMeterProperties().fill(this);
	}

	public MeterProperties immutable() {
		return this;
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();

		if (pos != null) {
			nbt.put("pos", pos.toNbt());
		}
		if (name != null) {
			nbt.putString("name", name);
		}
		if (color != null) {
			nbt.putInt("color", color);
		}
		if (movable != null) {
			nbt.putBoolean("movable", movable);
		}
		if (eventTypes != null) {
			nbt.putInt("event types", eventTypes);
		}

		return nbt;
	}

	public static MeterProperties fromNbt(NbtCompound nbt) {
		MeterProperties properties = new MeterProperties();

		if (nbt.contains("pos")) {
			properties.pos = DimPos.fromNbt(nbt.getCompound("pos"));
		}
		if (nbt.contains("name")) {
			properties.name = nbt.getString("name");
		}
		if (nbt.contains("color")) {
			properties.color = nbt.getInt("color");
		}
		if (nbt.contains("movable")) {
			properties.movable = nbt.getBoolean("movable");
		}
		if (nbt.contains("event types")) {
			properties.eventTypes = nbt.getInt("event types");
		}

		return properties;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		if (name != null) {
			json.addProperty("name", name);
		}
		if (color != null) {
			json.addProperty("color", ColorUtils.toRGBString(color));
		}
		if (movable != null) {
			json.addProperty("movable", movable);
		}
		if (eventTypes != null) {
			JsonArray types = new JsonArray();

			for (EventType type : EventType.ALL) {
				if (hasEventType(type)) {
					types.add(type.getLegacyKey());
				}
			}

			json.add("event_types", types);
		}

		return json;
	}

	public static MeterProperties fromJson(JsonObject json) {
		MeterProperties properties = new MeterProperties();

		if (json.has("name")) {
			JsonElement nameJson = json.get("name");

			if (nameJson.isJsonPrimitive()) {
				properties.name = nameJson.getAsString();
			}
		}
		if (json.has("color")) {
			JsonElement colorJson = json.get("color");

			if (colorJson.isJsonPrimitive()) {
				try {
					properties.color = ColorUtils.fromRGBString(colorJson.getAsString());
				} catch (NumberFormatException e) {

				}
			}
		}
		if (json.has("movable")) {
			JsonElement movableJson = json.get("movable");

			if (movableJson.isJsonPrimitive()) {
				properties.movable = movableJson.getAsBoolean();
			}
		}
		if (json.has("event_types")) {
			JsonElement typesJson = json.get("event_types");

			if (typesJson.isJsonArray()) {
				properties.eventTypes = 0;
				JsonArray types = typesJson.getAsJsonArray();

				for (int index = 0; index < types.size(); index++) {
					JsonElement typeJson = types.get(index);

					if (typeJson.isJsonPrimitive()) {
						String typeName = typeJson.getAsString();
						EventType type = EventType.byLegacyKey(typeName);

						if (type != null) {
							properties.eventTypes |= type.flag();
						}
					}
				}
			}
		}

		return properties;
	}

	public static class MutableMeterProperties extends MeterProperties {

		public boolean setPos(DimPos pos) {
			DimPos prevPos = super.pos;
			super.pos = pos;

			return prevPos == null || !prevPos.equals(pos);
		}

		public boolean setName(String name) {
			String prevName = super.name;
			super.name = name;

			return prevName == null || !prevName.equals(name);
		}

		public boolean setColor(Integer color) {
			Integer prevColor = super.color;
			super.color = color;

			return prevColor == null || !prevColor.equals(color);
		}

		public boolean setMovable(Boolean movable) {
			Boolean prevMovable = super.movable;
			super.movable = movable;

			return prevMovable == null || !prevMovable.equals(movable);
		}

		public boolean setEventTypes(Integer eventTypes) {
			Integer prevEventTypes = super.eventTypes;
			super.eventTypes = eventTypes;

			return prevEventTypes == null || !prevEventTypes.equals(eventTypes);
		}

		public boolean toggleEventType(EventType type) {
			if (super.eventTypes == null) {
				super.eventTypes = 0;
			}

			return setEventTypes(super.eventTypes ^ type.flag());
		}

		public MutableMeterProperties mutable() {
			return this;
		}

		public MeterProperties immutable() {
			return new MeterProperties(super.pos, super.name, super.color, super.movable, super.eventTypes);
		}

		/**
		 * If a property does not yet have a value, copy the value from the given properties.
		 */
		public MutableMeterProperties fill(MeterProperties properties) {
			if (properties == null) {
				return this;
			}

			if (super.pos == null) {
				super.pos = properties.pos;
			}
			if (super.name == null) {
				super.name = properties.name;
			}
			if (super.color == null) {
				super.color = properties.color;
			}
			if (super.movable == null) {
				super.movable = properties.movable;
			}
			if (super.eventTypes == null) {
				super.eventTypes = properties.eventTypes;
			}

			return this;
		}
	}
}
