package redstone.multimeter.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.util.Identifier;

import redstone.multimeter.RedstoneMultimeterMod;

public class SupplierRegistry<T> {
	
	private final Identifier id;
	private final Map<Class<? extends T>, Identifier> typeToId;
	private final Map<Identifier, Supplier<? extends T>> idToSupplier;
	
	public SupplierRegistry(String name) {
		this.id = new Identifier(RedstoneMultimeterMod.NAMESPACE, name);
		this.typeToId = new HashMap<>();
		this.idToSupplier = new HashMap<>();
	}
	
	public Identifier getRegistryId() {
		return id;
	}
	
	public T get(Identifier id) {
		Supplier<? extends T> objSupplier = idToSupplier.get(id);
		return objSupplier == null ? null : objSupplier.get();
	}
	
	public Identifier getId(T obj) {
		return typeToId.get(obj.getClass());
	}
	
	public <P extends T> void register(String name, Class<P> type, Supplier<P> supplier) {
		String namespace = id.getNamespace();
		String path = String.format("%s/%s", id.getPath(), name);
		Identifier id = new Identifier(namespace, path);
		
		if (typeToId.containsKey(type)) {
			throw new IllegalStateException("Registry " + this.id + " already registered an entry with type " + type);
		}
		if (idToSupplier.containsKey(id)) {
			throw new IllegalStateException("Registry " + this.id + " already registered an entry with id " + id);
		}
		
		typeToId.put(type, id);
		idToSupplier.put(id, supplier);
	}
}
