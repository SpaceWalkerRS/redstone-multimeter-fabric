package redstone.multimeter.util;

import net.minecraft.world.dimension.Dimension;

public class Dimensions {

	public static DimensionRegistry REGISTRY;

	public static void setUp() {
		REGISTRY = new DimensionRegistry();

		register(0);
		register(-1);
		register(1);
	}

	public static void register(int dimension) {
		Dimension dim = Dimension.fromId(dimension);

		if (dim != null) {
			register(dim, dimension);
		}
	}

	public static void register(Dimension dimension, int id) {
		String key = dimension.getName().toLowerCase().replace(' ', '_');

		if (!REGISTRY.containsKey(key)) {
			REGISTRY.put(key, id);
		}
	}
}
