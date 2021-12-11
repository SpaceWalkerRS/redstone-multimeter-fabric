package redstone.multimeter.util;

import net.minecraft.util.Identifier;

public class DimensionUtils {
	
	public static int getRawId(Identifier id) {
		switch (id.getPath()) {
		case "overworld":
			return 0;
		case "the_nether":
			return -1;
		case "the_end":
			return 1;
		}
		
		return 0;
	}
	
	public static Identifier getId(int rawId) {
		switch (rawId) {
		case 0:
			return new Identifier("overworld");
		case -1:
			return new Identifier("the_nether");
		case 1:
			return new Identifier("the_end");
			
		}
		
		return new Identifier("overworld");
	}
}
