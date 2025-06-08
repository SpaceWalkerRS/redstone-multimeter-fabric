package redstone.multimeter.util;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.registry.MappedRegistry;

public class DimensionRegistry extends MappedRegistry/*<String, Integer>*/ {

	private final Map<Integer, String> keys = ((BiMap/*<String, Integer>*/)entries).inverse();

	@Override
	protected Map<String, Integer> createMap() {
		return HashBiMap.create();
	}

	public String getKey(Integer value) {
		return keys.get(value);
	}
}
