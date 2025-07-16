package redstone.multimeter.client.option;

public interface Cyclable<T extends Cyclable<T>> {

	String key();

	static <T extends Enum<T> & Cyclable<T>> T byKey(Class<T> clazz, String key) {
		T[] values = clazz.getEnumConstants();

		for (T value : values) {
			if (value.key().equals(key)) {
				return value;
			}
		}
		
		throw new IllegalStateException("could not find value " + key + " for type " + clazz.getSimpleName());
	}

	static <T extends Enum<T> & Cyclable<T>> T next(Class<T> clazz, T value) {
		return fromOrdinal(clazz, value.ordinal() + 1);
	}

	static <T extends Enum<T> & Cyclable<T>> T prev(Class<T> clazz, T value) {
		return fromOrdinal(clazz, value.ordinal() - 1);
	}

	private static <T extends Enum<T> & Cyclable<T>> T fromOrdinal(Class<T> clazz, int ordinal) {
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
