package redstone.multimeter.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.resource.Identifier;

public class Blocks {

	private static Map<String, Block> BLOCKS;

	private static void setUp() {
		BLOCKS = new HashMap<>();

		for (Block block : Block.BY_ID) {
			if (block == null) {
				continue;
			}

			String key = block.getTranslationKey();

			if (key == null) {
				continue;
			}

			BLOCKS.put(key.substring("tile.".length()), block);
		}
	}

	public static Block byKey(Identifier key) {
		return byKey(key.getPath());
	}

	public static Block byKey(String key) {
		if (BLOCKS == null) {
			setUp();
		}

		return BLOCKS.get(key);
	}
}
