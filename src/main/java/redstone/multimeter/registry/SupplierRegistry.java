package redstone.multimeter.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import redstone.multimeter.RedstoneMultimeterMod;

public class SupplierRegistry<T> {

	private final String key;
	private final Map<Class<? extends T>, String> byKey;
	private final Map<String, Supplier<? extends T>> keys;

	public SupplierRegistry() {
		this(RedstoneMultimeterMod.NAMESPACE);
	}

	public SupplierRegistry(String key) {
		this.key = key;
		this.byKey = new HashMap<>();
		this.keys = new HashMap<>();
	}

	public String getRegistryKey() {
		return key;
	}

	public T get(String key) {
		Supplier<? extends T> supplier = keys.get(key);
		return supplier == null ? null : supplier.get();
	}

	public String getKey(T obj) {
		return byKey.get(obj.getClass());
	}

	public <P extends T> void register(String name, Class<P> type, Supplier<P> supplier) {
		String key = this.key + "|" + name;

		if (byKey.containsKey(type)) {
			throw new IllegalStateException("Registry " + this.key + " already registered an entry with type " + type);
		}
		if (keys.containsKey(key)) {
			throw new IllegalStateException("Registry " + this.key + " already registered an entry with key " + key);
		}

		byKey.put(type, key);
		keys.put(key, supplier);
	}
}
