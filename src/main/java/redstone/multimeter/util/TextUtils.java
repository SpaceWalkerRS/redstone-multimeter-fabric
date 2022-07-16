package redstone.multimeter.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.interfaces.mixin.IKeyBinding;

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
					
					Text line = Text.literal(subString);
					lines.add(line);
					
					break;
				}
			}
			
			if (length == text.length()) {
				if (length > 0) {
					Text line = Text.literal(text);
					lines.add(line);
				}
				
				break;
			}
			
			text = text.substring(length);
		}
		
		return lines;
	}
	
	public static void formatKeyValue(List<Text> lines, String key, Object value) {
		formatKeyValue(lines, key, value.toString());
	}
	
	public static void formatKeyValue(List<Text> lines, String key, String value) {
		lines.add(formatKeyValue(key, value));
	}
	
	public static void formatKeyValue(Tooltip tooltip, String key, Object value) {
		formatKeyValue(tooltip, key, value.toString());
	}
	
	public static void formatKeyValue(Tooltip tooltip, String key, String value) {
		tooltip.add(formatKeyValue(key, value));
	}
	
	public static Text formatKeyValue(String key, Object value) {
		return Text.literal("").
			append(Text.literal(key + ": ").formatted(Formatting.GOLD)).
			append(Text.literal(value.toString()));
	}
	
	public static void formatKeybind(List<Text> lines, Key... keys) {
		formatKeybind(lines, keys);
	}
	
	public static void formatKeybind(List<Text> lines, Key[]... keybindings) {
		lines.add(formatKeybind(keybindings));
	}
	
	public static void formatKeybind(Tooltip tooltip, Key... keys) {
		formatKeybind(tooltip, keys);
	}
	
	public static void formatKeybind(Tooltip tooltip, Key[]... keybindings) {
		tooltip.add(formatKeybind(keybindings));
	}
	
	public static MutableText formatKeybind(KeyBinding... keyBindings) {
		List<KeyBinding> boundKeyBindings = new ArrayList<>();

		for (KeyBinding keyBinding : keyBindings) {
			if (!keyBinding.isUnbound()) {
				boundKeyBindings.add(keyBinding);
			}
		}

		if (boundKeyBindings.isEmpty()) {
			return Text.literal("keybind: -");
		}
		
		Key[][] keybindings = new Key[keyBindings.length][];
		
		for (int i = 0; i < boundKeyBindings.size(); i++) {
			Key key = ((IKeyBinding)boundKeyBindings.get(i)).getBoundKeyRSMM();
			keybindings[i] = new Key[] { key };
		}
		
		return formatKeybind(keybindings);
	}
	
	public static MutableText formatKeybind(Key... keys) {
		return formatKeybind(keys);
	}
	
	public static MutableText formatKeybind(Key[]... keybindings) {
		MutableText text = Text.literal("").
			append(Text.literal("keybind: ").formatted(Formatting.GOLD));
		
		int i = 0;
		
		for (Key[] keys : keybindings) {
			int j = 0;
			
			if (i++ > 0) {
				text.append(" OR ");
			}
			
			for (Key key : keys) {
				if (j++ > 0) {
					text.append(" + ");
				}
				
				text.append(formatKey(key));
			}
		}
		
		return text;
	}
	
	public static MutableText formatKey(KeyBinding keybind) {
		return keybind.isUnbound() ? Text.literal("-").formatted(Formatting.YELLOW) : formatKey(((IKeyBinding)keybind).getBoundKeyRSMM());
	}
	
	public static MutableText formatKey(Key key) {
		return key.getLocalizedText().copy().formatted(Formatting.YELLOW);
	}
	
	public static MutableText formatKey(String key) {
		return Text.literal(key).formatted(Formatting.YELLOW);
	}
}
