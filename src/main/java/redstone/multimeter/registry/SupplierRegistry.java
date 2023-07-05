package redstone.multimeter.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.resource.Identifier;

import redstone.multimeter.RedstoneMultimeterMod;

public class SupplierRegistry<T> {

	private final Identifier key;
	private final Map<Class<? extends T>, Identifier> byKey;
	private final Map<Identifier, Supplier<? extends T>> keys;

	public SupplierRegistry(String name) {
		this(new Identifier(RedstoneMultimeterMod.NAMESPACE, name));
	}

	public SupplierRegistry(Identifier key) {
		this.key = key;
		this.byKey = new HashMap<>();
		this.keys = new HashMap<>();
	}

	public Identifier getRegistryKey() {
		return key;
	}

	public T get(Identifier key) {
		Supplier<? extends T> supplier = keys.get(key);
		return supplier == null ? null : supplier.get();
	}

	public Identifier getKey(T obj) {
		return byKey.get(obj.getClass());
	}

	public <P extends T> void register(String name, Class<P> type, Supplier<P> supplier) {
		String namespace = key.getNamespace();
		String path = String.format("%s/%s", key.getPath(), name);
		Identifier key = new Identifier(namespace, path);

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
