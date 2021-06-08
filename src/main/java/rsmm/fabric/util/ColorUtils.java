package rsmm.fabric.util;

import java.awt.Color;

public class ColorUtils {
	
	private static int colorIndex = 0;
	
	public static int getAlpha(int color) {
		return (color >> 24) & 0xFF;
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
	
	public static int nextColor() {
		return nextColor(true);
	}
	
	public static int nextColor(boolean cycleIndex) {
		float hue = ((colorIndex * 11) % 8 + (colorIndex / 8) / 2.0F) / 8.0F;
		
		if (cycleIndex) {
			colorIndex = (colorIndex + 1) % 16;
		}
		
		return HSBToRGB(hue, 0.7F, 1.0F);
	}
	
	public static int HSBToRGB(float hue, float saturation, float brightness) {
		Color hsb = Color.getHSBColor(hue, saturation, brightness);
		
		int r = hsb.getRed();
		int g = hsb.getGreen();
		int b = hsb.getBlue();
		
		return fromRGB(r, g, b);
	}
	
	public static int fromRGB(int red, int green, int blue) {
		return fromARGB(0xFF, red, green, blue);
	}
	
	public static int fromARGB(int alpha, int red, int green, int blue) {
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
	
	public static int fromRGBString(String string) {
		if (string.length() > 6) {
			throw new NumberFormatException("Too many characters!");
		}
		
		return 0xFF000000 | Integer.valueOf(string, 16);
	}
	
	public static String toRGBString(int color) {
		String hex = Integer.toHexString(color & 0xFFFFFF);
		
		while (hex.length() < 6) {
			hex = "0" + hex;
		}
		
		return hex;
	}
}
