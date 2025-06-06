package redstone.multimeter.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class Items {

	private static Map<String, Item> ITEMS;

	private static void setUp() {
		ITEMS = new HashMap<>();

		for (Item item : Item.BY_ID) {
			if (item == null) {
				continue;
			}

			String key = item.getTranslationKey();

			if (key == null) {
				continue;
			}

			ITEMS.put(key.substring("item.".length()), item);
		}
	}

	public static Item byBlock(Block block) {
		return Item.BY_ID[block.id];
	}

	public static Item byKey(String key) {
		if (ITEMS == null) {
			setUp();
		}

		return ITEMS.get(key);
	}
}
