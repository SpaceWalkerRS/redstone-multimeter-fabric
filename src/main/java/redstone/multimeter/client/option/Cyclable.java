package redstone.multimeter.client.option;

public interface Cyclable<T extends Cyclable<T>> {

	String key();

	String legacyKey();

	static <T extends Enum<T> & Cyclable<T>> T byKey(Class<T> clazz, String key) {
		T[] values = clazz.getEnumConstants();

		for (T value : values) {
			if (value.key().equals(key)) {
				return value;
			}
		}

		return null;
	}

	static <T extends Enum<T> & Cyclable<T>> T byLegacyKey(Class<T> clazz, String legacyKey) {
		T[] values = clazz.getEnumConstants();

		for (T value : values) {
			if (value.legacyKey().equals(legacyKey)) {
				return value;
			}
		}

		return null;
	}

	static <T extends Enum<T> & Cyclable<T>> T next(Class<T> clazz, T value) {
		return fromOrdinal(clazz, value.ordinal() + 1);
	}

	static <T extends Enum<T> & Cyclable<T>> T prev(Class<T> clazz, T value) {
		return fromOrdinal(clazz, value.ordinal() - 1);
	}

	public static <T extends Enum<T> & Cyclable<T>> T fromOrdinal(Class<T> clazz, int ordinal) {
		T[] values = clazz.getEnumConstants();

		if (ordinal < 0) {
			ordinal = values.length - 1;
		}
		if (ordinal >= values.length) {
			ordinal = 0;
		}

		return values[ordinal];
	}
}
