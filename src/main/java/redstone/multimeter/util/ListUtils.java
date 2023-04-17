package redstone.multimeter.util;

import java.util.List;
import java.util.function.Predicate;

public class ListUtils {

	public static <T> int binarySearch(List<T> list, Predicate<T> tooLow) {
		return binarySearch(list, 0, list.size() - 1, tooLow);
	}

	public static <T> int binarySearch(List<T> list, int low, int high, Predicate<T> tooLow) {
		if (list.isEmpty()) {
			return -1;
		}

		while (high > low) {
			int mid = (low + high) / 2;

			if (tooLow.test(list.get(mid))) {
				low = mid + 1;
			} else {
				high = mid;
			}
		}

		return low;
	}
}
