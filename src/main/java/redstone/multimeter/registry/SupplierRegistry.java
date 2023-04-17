package redstone.multimeter.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.RedstoneMultimeterMod;

public class SupplierRegistry<T> {

	private final ResourceLocation key;
	private final Map<Class<? extends T>, ResourceLocation> byKey;
	private final Map<ResourceLocation, Supplier<? extends T>> keys;

	public SupplierRegistry(String name) {
		this(new ResourceLocation(RedstoneMultimeterMod.NAMESPACE, name));
	}

	public SupplierRegistry(ResourceLocation key) {
		this.key = key;
		this.byKey = new HashMap<>();
		this.keys = new HashMap<>();
	}

	public ResourceLocation getRegistryKey() {
		return key;
	}

	public T get(ResourceLocation key) {
		Supplier<? extends T> supplier = keys.get(key);
		return supplier == null ? null : supplier.get();
	}

	public ResourceLocation getKey(T obj) {
		return byKey.get(obj.getClass());
	}

	public <P extends T> void register(String name, Class<P> type, Supplier<P> supplier) {
		String namespace = key.getNamespace();
		String path = String.format("%s/%s", key.getPath(), name);
		ResourceLocation key = new ResourceLocation(namespace, path);

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
