package redstone.multimeter.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextUtils {
	
	private static final int MAX_WIDTH = 200;
	
	public static List<Text> toLines(TextRenderer font, String text) {
		List<Text> lines = new ArrayList<>();
		
		while (!text.isEmpty()) {
			int lastSpace = -1;
			int length = 0;
			
			while (length++ < text.length()) {
				if (length == text.length()) {
					break;
				}
				
				int index = length - 1;
				
				if (text.charAt(index) == ' ') {
					lastSpace = index;
				}
				
				String subString = text.substring(0, length);
				
				if (font.getWidth(subString) > MAX_WIDTH) {
					if (lastSpace >= 0) {
						subString = text.substring(0, lastSpace);
						length = lastSpace + 1;
					}
					
					Text line = new LiteralText(subString);
					lines.add(line);
					
					break;
				}
			}
			
			if (length == text.length()) {
				if (length > 0) {
					Text line = new LiteralText(text);
					lines.add(line);
				}
				
				break;
			}
			
			text = text.substring(length);
		}
		
		return lines;
	}
	
	public static void addFancyText(List<Text> lines, String title, Object info) {
		addFancyText(lines, title, info.toString());
	}
	
	public static void addFancyText(List<Text> lines, String title, String info) {
		lines.add(formatFancyText(title, info));
	}
	
	public static Text formatFancyText(String title, Object info) {
		return new LiteralText("").
			append(new LiteralText(title + ": ").formatted(Formatting.GOLD)).
			append(new LiteralText(info.toString()));
	}
}
