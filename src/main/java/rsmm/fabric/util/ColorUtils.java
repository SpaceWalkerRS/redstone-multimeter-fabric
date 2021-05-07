package rsmm.fabric.util;

import java.awt.Color;

public class ColorUtils {
	
	public static final int MAX_COLOR = 256 * 256 * 256;
	
	private static int colorIndex = 0;
	
	public static int nextColor() {
		return nextColor(true);
	}
	
	public static int nextColor(boolean cycleIndex) {
		float hue = ((colorIndex * 11) % 8 + (colorIndex / 8) / 2.0F) / 8.0F;
		int color = hsbToInt(hue, 0.7F, 1.0F);
		
		if (cycleIndex) {
			colorIndex = (colorIndex + 1) % 16;
		}
		
		return color;
	}
	
	public static int hsbToInt(float hue, float saturation, float brightness) {
		Color hsb = Color.getHSBColor(hue, saturation, brightness);
		
		int r = hsb.getRed();
		int g = hsb.getGreen();
		int b = hsb.getBlue();
		
		return fromRGB(r, g, b);
	}
	
	public static int fromRGB(int red, int green, int blue) {
		return 0xFF000000 | (red << 16) | (green << 8) | blue;
	}
	
	public static int fromString(String string) {
		if (string.length() > 6) {
			throw new NumberFormatException("Too many characters!");
		}
		
		while (string.length() < 6) {
			string += "0";
		}
		
		int r = Integer.valueOf(string.substring(0, 2), 16);
		int g = Integer.valueOf(string.substring(2, 4), 16);
		int b = Integer.valueOf(string.substring(4, 6), 16);
		
		return fromRGB(r, g, b);
	}
	
	public static String toHexString(int color) {
		String r = Integer.toHexString(getRed(color));
		while (r.length() < 2) {
			r = "0" + r;
		}
		String g = Integer.toHexString(getGreen(color));
		while (g.length() < 2) {
			g = "0" + g;
		}
		String b = Integer.toHexString(getBlue(color));
		while (b.length() < 2) {
			b = "0" + b;
		}
		
		return r + g + b;
	}
	
	public static int getRed(int color) {
		return (color >> 16) & 0xFF;
	}
	
	public static int getGreen(int color) {
		return (color >> 8) & 0xFF;
	}
	
	public static int getBlue(int color) {
		return color & 0xFF;
	}
}
