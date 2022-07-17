package redstone.multimeter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.KeyCode;
import net.minecraft.text.LiteralText;
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
	
	public static void formatKeybind(List<Text> lines, KeyCode... keys) {
		formatKeybind(lines, keys);
	}
	
	public static void formatKeybind(List<Text> lines, KeyCode[]... keybindings) {
		lines.add(formatKeybind(keybindings));
	}
	
	public static void formatKeybind(Tooltip tooltip, KeyCode... keys) {
		formatKeybind(tooltip, keys);
	}
	
	public static void formatKeybind(Tooltip tooltip, KeyCode[]... keybindings) {
		tooltip.add(formatKeybind(keybindings));
	}
	
	public static Text formatKeybind(KeyBinding... keyBindings) {
		List<KeyBinding> boundKeyBindings = new ArrayList<>();

		for (KeyBinding keyBinding : keyBindings) {
			if (!keyBinding.isNotBound()) {
				boundKeyBindings.add(keyBinding);
			}
		}

		if (boundKeyBindings.isEmpty()) {
			return new LiteralText("keybind: -");
		}
		
		KeyCode[][] keybindings = new KeyCode[keyBindings.length][];
		
		for (int i = 0; i < boundKeyBindings.size(); i++) {
			KeyCode key = ((IKeyBinding)boundKeyBindings.get(i)).getBoundKeyRSMM();
			keybindings[i] = new KeyCode[] { key };
		}
		
		return formatKeybind(keybindings);
	}
	
	public static Text formatKeybind(KeyCode... keys) {
		return formatKeybind(keys);
	}
	
	public static Text formatKeybind(KeyCode[]... keybindings) {
		Text text = new LiteralText("").
			append(new LiteralText("keybind: ").formatted(Formatting.GOLD));
		
		int i = 0;
		
		for (KeyCode[] keys : keybindings) {
			int j = 0;
			
			if (i++ > 0) {
				text.append(" OR ");
			}
			
			for (KeyCode key : keys) {
				if (j++ > 0) {
					text.append(" + ");
				}
				
				text.append(formatKey(key));
			}
		}
		
		return text;
	}
	
	public static Text formatKey(KeyBinding keybind) {
		return keybind.isNotBound() ? new LiteralText("-").formatted(Formatting.YELLOW) : formatKey(((IKeyBinding)keybind).getBoundKeyRSMM());
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
}
