package redstone.multimeter.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class IdRegistry {

	private final Map<String, Integer> values = new HashMap<>();
	private final Int2ObjectMap<String> keys = new Int2ObjectOpenHashMap<>();

	public Integer get(String key) {
		return values.get(key);
	}

	public void put(String key, Integer value) {
		if (value == null) {
			throw new NullPointerException();
		} else {
			put(key, value.intValue());
		}
	}

	public void put(String key, int value) {
		if (!values.containsKey(key)) {
			values.put(key, value);
		}

		keys.put(value, key);
	}

	public String getKey(int value) {
		return keys.get(value);
	}

	public boolean containsKey(String key) {
		return values.containsKey(key);
	}

	public Set<String> keySet() {
		return values.keySet();
	}
}
