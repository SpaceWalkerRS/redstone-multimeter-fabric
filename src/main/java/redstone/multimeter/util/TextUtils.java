package redstone.multimeter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.siphalor.amecs.api.KeyModifier;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import redstone.multimeter.client.compat.amecs.AmecsHelper;
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
	
	public static MutableText formatKeybindInfo(Object... keybinds) {
		MutableText text = Text.literal("keybind: ");
		Collection<Object> boundKeybinds = filterUnboundKeybinds(keybinds);

		if (boundKeybinds.isEmpty()) {
			return text.append("-");
		}

		int i = 0;

		for (Object o : boundKeybinds) {
			if (i++ > 0) {
				text.append(" OR ");
			}

			if (o instanceof KeyBinding) {
				text.append(formatKeybind((KeyBinding)o));
			} else
			if (o instanceof Key) {
				text.append(formatKeybind((Key)o));
			} else
			if (o instanceof Key[]) {
				text.append(formatKeybind((Key[])o));
			} else
			if (o instanceof Object[]) {
				text.append(formatKeybind((Object[])o));
			}
		}

		return text;
	}

	private static Collection<Object> filterUnboundKeybinds(Object... keybinds) {
		Collection<Object> boundKeybinds = new LinkedList<>();

		for (Object o : keybinds) {
			if (o instanceof KeyBinding) {
				KeyBinding keybind = (KeyBinding)o;

				if (keybind.isUnbound()) {
					continue;
				}
			}

			boundKeybinds.add(o);
		}

		return boundKeybinds;
	}
	
	public static MutableText formatKeybind(KeyBinding keybind) {
		MutableText text = Text.literal("");

		if (keybind.isUnbound()) {
			return text;
		}
		
		Key boundKey = ((IKeyBinding)keybind).getBoundKeyRSMM();
		Collection<KeyModifier> modifiers = AmecsHelper.getKeyModifiers(keybind);
		
		for (KeyModifier modifier : modifiers) {
			text.
				append(formatKey(AmecsHelper.getModifierName(modifier))).
				append(" + ");
		}
		
		text.append(formatKey(boundKey));
		
		return text;
	}
	
	public static MutableText formatKeybind(Key... keys) {
		MutableText text = Text.literal("");
		
		for (int i = 0; i < keys.length; i++) {
			Key key = keys[i];
			
			if (i > 0) {
				text.append(" + ");
			}
			
			text.append(formatKey(key));
		}
		
		return text;
	}

	public static MutableText formatKeybind(Object... keys) {
		Collection<Text> formattedKeys = new LinkedList<>();

		for (Object o : keys) {
			if (o instanceof KeyBinding) {
				formattedKeys.add(formatKeybind((KeyBinding)o));
			} else
			if (o instanceof Key) {
				formattedKeys.add(formatKey((Key)o));
			} else 
			if (o instanceof String) {
				formattedKeys.add(formatKey((String)o));
			}
		}

		MutableText text = Text.literal("");

		for (Text key : formattedKeys) {
			text.append(key);
		}

		return text;
	}
	
	public static MutableText formatKey(Key key) {
		return key.getLocalizedText().copy().formatted(Formatting.YELLOW);
	}
	
	public static MutableText formatKey(String key) {
		return Text.literal(key).formatted(Formatting.YELLOW);
	}
	
	public static MutableText formatKey(Text text) {
		return text.copy().formatted(Formatting.YELLOW);
	}
}
