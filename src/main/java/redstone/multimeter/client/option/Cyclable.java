package redstone.multimeter.client.option;

public interface Cyclable<T extends Cyclable<T>> {

	public String getName();

	public static <T extends Enum<T> & Cyclable<T>> T next(Class<T> clazz, T value) {
		return fromOrdinal(clazz, value.ordinal() + 1);
	}

	public static <T extends Enum<T> & Cyclable<T>> T prev(Class<T> clazz, T value) {
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
