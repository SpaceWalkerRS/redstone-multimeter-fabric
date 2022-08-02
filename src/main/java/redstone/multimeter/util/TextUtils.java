package redstone.multimeter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.siphalor.amecs.api.KeyModifier;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.KeyCode;
import net.minecraft.text.LiteralText;
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
				
				if (font.getStringWidth(subString) > MAX_WIDTH) {
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
		return new LiteralText("").
			append(new LiteralText(key + ": ").formatted(Formatting.GOLD)).
			append(new LiteralText(value.toString()));
	}
	
	public static Text formatKeybindInfo(Object... keybinds) {
		Text text = new LiteralText("").
				append(new LiteralText("keybind: ").formatted(Formatting.GOLD));
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
			if (o instanceof KeyCode) {
				text.append(formatKeybind((KeyCode)o));
			} else
			if (o instanceof KeyCode[]) {
				text.append(formatKeybind((KeyCode[])o));
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

				if (keybind.isNotBound()) {
					continue;
				}
			}

			boundKeybinds.add(o);
		}

		return boundKeybinds;
	}
	
	public static Text formatKeybind(KeyBinding keybind) {
		Text text = new LiteralText("");

		if (keybind.isNotBound()) {
			return text;
		}
		
		KeyCode boundKey = ((IKeyBinding)keybind).getBoundKeyRSMM();
		Collection<KeyModifier> modifiers = AmecsHelper.getKeyModifiers(keybind);
		
		for (KeyModifier modifier : modifiers) {
			text.
				append(formatKey(AmecsHelper.getModifierName(modifier))).
				append(" + ");
		}
		
		text.append(formatKey(boundKey));
		
		return text;
	}
	
	public static Text formatKeybind(KeyCode... keys) {
		Text text = new LiteralText("");
		
		for (int i = 0; i < keys.length; i++) {
			KeyCode key = keys[i];
			
			if (i > 0) {
				text.append(" + ");
			}
			
			text.append(formatKey(key));
		}
		
		return text;
	}

	public static Text formatKeybind(Object... keys) {
		List<Text> formattedKeys = new ArrayList<>();

		for (Object o : keys) {
			if (o instanceof KeyBinding) {
				formattedKeys.add(formatKeybind((KeyBinding)o));
			} else
			if (o instanceof KeyCode) {
				formattedKeys.add(formatKey((KeyCode)o));
			} else 
			if (o instanceof String) {
				formattedKeys.add(formatKey((String)o));
			}
		}

		Text text = new LiteralText("");

		for (int i = 0; i < formattedKeys.size(); i++) {
			Text key = formattedKeys.get(i);

			if (i > 0) {
				text.append(" + ");
			}

			text.append(key);
		}

		return text;
	}
	
	public static Text formatKey(KeyCode key) {
		int code  = key.getKeyCode();
		String translationKey = key.getName();
		
		String keyName = null;
		
		switch (key.getCategory()) {
		case KEYSYM:
			keyName = InputUtil.getKeycodeName(code);
			break;
		case SCANCODE:
			keyName = InputUtil.getScancodeName(code);
			break;
		case MOUSE:
			String buttonName = I18n.translate(translationKey);
			
			if (Objects.equals(translationKey, buttonName)) {
				keyName = I18n.translate(InputUtil.Type.MOUSE.getName(), code + 1);
			} else {
				keyName = buttonName;
			}
		}
		if (keyName == null) {
			keyName = I18n.translate(translationKey);
		}
		
		return formatKey(keyName);
	}
	
	public static Text formatKey(String key) {
		return new LiteralText(key).formatted(Formatting.YELLOW);
	}
	
	public static Text formatKey(Text text) {
		return text.copy().formatted(Formatting.YELLOW);
	}
}
