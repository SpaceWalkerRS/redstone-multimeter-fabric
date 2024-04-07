package redstone.multimeter.util;

import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public class DimensionUtils {

	public static String getKey(Dimension dimension) {
		return getKey(dimension.getType());
	}

	public static String getKey(DimensionType dimension) {
		return dimension.getName().toLowerCase().replace(' ', '_');
	}

	public static DimensionType byKey(String key) {
		for (DimensionType dimension : DimensionType.values()) {
			if (getKey(dimension).equals(key)) {
				return dimension;
			}
		}

		throw new RuntimeException("unknown dimension: " + key);
	}
}
