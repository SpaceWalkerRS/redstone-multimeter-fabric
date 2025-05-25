package redstone.multimeter.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.dimension.Dimension;

public class DimensionUtils {

	public static Map<String, Dimension> BY_KEY;

	public static String getKey(Dimension dimension) {
		return dimension.m_9807986().toLowerCase().replace(' ', '_');
	}

	public static Dimension byKey(String key) {
		return BY_KEY.get(key);
	}

	public static void setUp() {
		BY_KEY = new HashMap<>();
	}

	public static void register(Dimension dimension) {
		if (BY_KEY != null) {
			BY_KEY.put(getKey(dimension), dimension);
		}
	}

	public static void destroy() {
		BY_KEY = null;
	}
}
