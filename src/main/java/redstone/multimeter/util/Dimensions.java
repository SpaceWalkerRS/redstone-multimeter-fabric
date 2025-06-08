package redstone.multimeter.util;

import net.minecraft.util.registry.IdRegistry;
import net.minecraft.world.dimension.DimensionType;

public class Dimensions {

	public static IdRegistry<String, DimensionType> REGISTRY;

	public static void setUp() {
		REGISTRY = new IdRegistry<>();

		for (DimensionType dimension : DimensionType.values()) {
			REGISTRY.put(dimension.getKey(), dimension);
		}
	}
}
