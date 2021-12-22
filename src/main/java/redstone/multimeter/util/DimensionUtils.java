package redstone.multimeter.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.class_2750;
import net.minecraft.util.Identifier;

public class DimensionUtils {
	
	private static final Map<Identifier, class_2750> ID_TO_TYPE;
	private static final Map<class_2750, Identifier> TYPE_TO_ID;
	
	private static void register(Identifier id, class_2750 type) {
		ID_TO_TYPE.put(id, type);
		TYPE_TO_ID.put(type, id);
	}
	
	private static void register(String name, class_2750 type) {
		register(new Identifier(name), type);
	}
	
	public static class_2750 getType(Identifier id) {
		return ID_TO_TYPE.get(id);
	}
	
	public static Identifier getId(class_2750 type) {
		return TYPE_TO_ID.get(type);
	}
	
	static {
		
		ID_TO_TYPE = new HashMap<>();
		TYPE_TO_ID = new HashMap<>();
		
		for (class_2750 type : class_2750.values()) {
			register(type.method_11794(), type);
		}
	}
}
