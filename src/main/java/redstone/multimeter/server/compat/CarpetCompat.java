package redstone.multimeter.server.compat;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public class CarpetCompat {

	private final Supplier<Boolean> frozen;

	public CarpetCompat() {
		Class<?> TickSpeed;
		Field TickSpeed$process_entities;

		Supplier<Boolean> frozen = () -> false;

		try {
			TickSpeed = Class.forName("carpet.helpers.TickSpeed");
			TickSpeed$process_entities = TickSpeed.getField("process_entities");

			frozen = () -> {
				try {
					return !TickSpeed$process_entities.getBoolean(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					return false;
				}
			};
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
		}

		this.frozen = frozen;
	}

	public boolean isFrozen() {
		return frozen.get();
	}
}
