package redstone.multimeter.util;

import net.minecraft.item.Item;

public class Items {

	public static IdRegistry REGISTRY;

	public static void setUp() {
		REGISTRY = new IdRegistry();

		for (Item item : Item.BY_ID) {
			if (item == null) {
				continue;
			}

			String key = item.getTranslationKey();

			if (key == null) {
				continue;
			}

			REGISTRY.put(key.substring("item.".length()), item.id);
		}
	}
}
