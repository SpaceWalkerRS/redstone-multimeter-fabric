package rsmm.fabric.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.util.Identifier;

import rsmm.fabric.RedstoneMultimeterMod;

public class SupplierClazzRegistry<T> {
	
	private final Identifier id;
	private final Map<Class<? extends T>, Identifier> clazzToId;
	private final Map<Identifier, Supplier<? extends T>> idToSupplier;
	
	public SupplierClazzRegistry(String name) {
		this.id = new Identifier(RedstoneMultimeterMod.NAMESPACE, name);
		this.clazzToId = new HashMap<>();
		this.idToSupplier = new HashMap<>();
	}
	
	public Identifier getRegistryId() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public <P extends T> P get(Identifier id) {
		Supplier<? extends T> objSupplier = idToSupplier.get(id);
		return objSupplier == null ? null : (P)objSupplier.get();
	}
	
	public <P extends T> Identifier getId(P obj) {
		return clazzToId.get(obj.getClass());
	}
	
	public <P extends T> void register(String name, Class<P> clazz, Supplier<P> supplier) {
		String namespace = id.getNamespace();
		String path = String.format("%s/%s", id.getPath(), name);
		Identifier id = new Identifier(namespace, path);
		
		if (clazzToId.containsKey(clazz)) {
			throw new IllegalStateException("Registry " + this.id + " already registered an entry with clazz " + clazz);
		}
		if (idToSupplier.containsKey(id)) {
			throw new IllegalStateException("Registry " + this.id + " already registered an entry with id " + id);
		}
		
		clazzToId.put(clazz, id);
		idToSupplier.put(id, supplier);
	}
}
