package redstone.multimeter.util;

import net.minecraft.world.dimension.Dimension;

public class Dimensions {

	public static IdRegistry REGISTRY;

	public static void setUp() {
		REGISTRY = new IdRegistry();

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
		String key = dimension.m_9807986().toLowerCase().replace(' ', '_');

		if (!REGISTRY.containsKey(key)) {
			REGISTRY.put(key, id);
		}
	}
}
