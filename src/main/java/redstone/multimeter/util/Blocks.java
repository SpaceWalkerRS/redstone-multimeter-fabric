package redstone.multimeter.util;

import net.minecraft.block.Block;

public class Blocks {

	public static IdRegistry REGISTRY;

	public static void setUp() {
		REGISTRY = new IdRegistry();

		for (Block block : Block.BY_ID) {
			if (block == null) {
				continue;
			}

			String key = block.getTranslationKey();

			if (key == null) {
				continue;
			}

			REGISTRY.put(key.substring("tile.".length()), block.id);
		}

		REGISTRY.put("air", 0);
	}
}
