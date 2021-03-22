package rsmm.fabric.util;

import java.awt.Color;

public class ColorUtils {
	
	public static final int MAX_COLOR = 256 * 256 * 256;
	
	private static int colorIndex = 0;
	
	public static int nextColor() {
		float hue = ((colorIndex * 11) % 8 + (colorIndex / 8) / 2.0F) / 8.0F;
		int color = hsbToInt(hue, 0.7F, 1.0F);
		
		colorIndex = (colorIndex + 1) % 16;
		
		return color;
	}
	
	public static int hsbToInt(float h, float s, float b) {
        Color c = Color.getHSBColor(h,s,b);
        
        int color = 0xFF000000;
        color |= c.getBlue();
        color |= c.getGreen() << 8;
        color |= c.getRed() << 16;
        
        return color;
    }
}
