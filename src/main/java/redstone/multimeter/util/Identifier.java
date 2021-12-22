package redstone.multimeter.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import redstone.multimeter.RedstoneMultimeterMod;

public class Identifier {
	
	private final String namespace;
	private final String path;
	
	public Identifier(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
		
		validate();
	}
	
	public Identifier(String arg) {
		String namespace = RedstoneMultimeterMod.MINECRAFT_NAMESPACE;
		String path = arg;
		
		int index = arg.indexOf(':');
		
		if (index >= 0) {
			path = arg.substring(index + 1);
			
			if (index > 1) {
				namespace = arg.substring(0, index);
			}
		}
		
		this.namespace = namespace;
		this.path = path;
		
		validate();
	}
	
	private void validate() {
		if (!isValid(namespace, path)) {
			throw new IllegalStateException("Identifier \'" + toString() + "\' contains illegal characters!");
		}
	}
	
	public static boolean isValid(String namespace, String path) {
		return isValidNamespace(namespace) && isValidPath(path);
	}
	
	public static boolean isValidNamespace(String namespace) {
		return namespace.chars().allMatch(chr -> {
			return chr == '-' || chr == '.' || chr == '_' || (chr >= 'a' && chr <= 'z') || (chr >= '0' && chr <= '9');
		});
	}
	
	public static boolean isValidPath(String namespace) {
		return namespace.chars().allMatch(chr -> {
			return chr == '-' || chr == '.' || chr == '/' || chr == '_' || (chr >= 'a' && chr <= 'z') || (chr >= '0' && chr <= '9');
		});
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Identifier) {
			Identifier id = (Identifier)obj;
			return id.namespace.equals(namespace) && id.path.equals(path);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * namespace.hashCode() + path.hashCode();
	}
	
	@Override
	public String toString() {
		return namespace + ":" + path;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getPath() {
		return path;
	}
	
	@Environment(EnvType.CLIENT)
	public net.minecraft.util.Identifier toMC() {
		return new net.minecraft.util.Identifier(namespace, path);
	}
	
	@Environment(EnvType.CLIENT)
	public static Identifier of(net.minecraft.util.Identifier id) {
		return new Identifier(id.getNamespace(), id.getPath());
	}
}
