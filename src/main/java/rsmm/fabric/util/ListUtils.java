package rsmm.fabric.util;

import java.util.List;
import java.util.function.Function;

public class ListUtils {
	
	public static <T> int binarySearch(List<T> list, Function<T, Boolean> tooLow) {
		return binarySearch(list, 0, list.size() - 1, tooLow);
	}
	
	public static <T> int binarySearch(List<T> list, int low, int high, Function<T, Boolean> tooLow) {
		if (list.isEmpty()) {
			return -1;
		}
		//System.out.println("UP - low: " + low + " - high: " + high);
		while (high > low) {
			int mid = (low + high) / 2;
			//System.out.println("mid: " + mid + " - low: " + low + " - high: " + high);
			if (tooLow.apply(list.get(mid))) {
				low = mid + 1;
			} else {
				high = mid;
			}
		}
		//System.out.println("found " + low);
		return low;
	}
}
