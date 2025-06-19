package redstone.multimeter.util;

import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.util.registry.MappedRegistry;

public class IdRegistry extends MappedRegistry<String, Integer> {

	private final Int2ObjectMap<String> keys = new Int2ObjectOpenHashMap<>();

	@Override
	public Integer get(String key) {
		return (Integer) super.get(key);
	}

	@Override
	public void put(String key, Integer value) {
		if (value == null) {
			throw new NullPointerException();
		} else {
			put(key, value.intValue());
		}
	}

	public void put(String key, int value) {
		if (!entries.containsKey(key)) {
			entries.put(key, value);
		}

		keys.put(value, key);
	}

	public String getKey(int value) {
		return keys.get(value);
	}

	public boolean containsKey(String key) {
		return entries.containsKey(key);
	}

	public Set<String> keySet() {
		return entries.keySet();
	}
}
