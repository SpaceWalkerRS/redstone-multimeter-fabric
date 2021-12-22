package redstone.multimeter.util;

import java.util.HashMap;
import java.util.Map;

public class DimensionUtils {
	
	private static final Map<Identifier, Integer> ID_TO_RAW_ID;
	private static final Map<Integer, Identifier> RAW_ID_TO_ID;
	
	public static final int TYPE_OVERWORLD;
	public static final int TYPE_NETHER;
	public static final int TYPE_END;
	
	private static void register(Identifier id, int rawId) {
		ID_TO_RAW_ID.put(id, rawId);
		RAW_ID_TO_ID.put(rawId, id);
	}
	
	private static void register(String name, int rawId) {
		register(new Identifier(name), rawId);
	}
	
	public static Integer getRawId(Identifier id) {
		return ID_TO_RAW_ID.get(id);
	}
	
	public static Identifier getId(int rawId) {
		return RAW_ID_TO_ID.get(rawId);
	}
	
	static {
		
		RAW_ID_TO_ID = new HashMap<>();
		ID_TO_RAW_ID = new HashMap<>();
		
		TYPE_OVERWORLD = 0;
		TYPE_NETHER = -1;
		TYPE_END = 1;
		
		register("overworld", TYPE_OVERWORLD);
		register("the_nether", TYPE_NETHER);
		register("the_end", TYPE_END);
	}
}
