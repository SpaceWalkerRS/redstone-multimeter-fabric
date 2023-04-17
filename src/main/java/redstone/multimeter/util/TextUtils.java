package redstone.multimeter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import redstone.multimeter.client.compat.amecs.AmecsHelper;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.interfaces.mixin.IKeyMapping;

public class TextUtils {

	private static final int MAX_WIDTH = 200;

	public static List<Component> toLines(Font font, String text) {
		List<Component> lines = new ArrayList<>();

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

				if (font.width(subString) > MAX_WIDTH) {
					if (lastSpace >= 0) {
						subString = text.substring(0, lastSpace);
						length = lastSpace + 1;
					}

					Component line = new TextComponent(subString);
					lines.add(line);

					break;
				}
			}

			if (length == text.length()) {
				if (length > 0) {
					Component line = new TextComponent(text);
					lines.add(line);
				}

				break;
			}

			text = text.substring(length);
		}

		return lines;
	}

	public static void formatKeyValue(List<Component> lines, String key, Object value) {
		formatKeyValue(lines, key, value.toString());
	}

	public static void formatKeyValue(List<Component> lines, String key, String value) {
		lines.add(formatKeyValue(key, value));
	}

	public static void formatKeyValue(Tooltip tooltip, String key, Object value) {
		formatKeyValue(tooltip, key, value.toString());
	}

	public static void formatKeyValue(Tooltip tooltip, String key, String value) {
		tooltip.add(formatKeyValue(key, value));
	}

	public static Component formatKeyValue(String key, Object value) {
		return new TextComponent("").append(new TextComponent(key + ": ").withStyle(ChatFormatting.GOLD))
			.append(new TextComponent(value.toString()));
	}

	public static Component formatKeybindInfo(Object... keybinds) {
		Component component = new TextComponent("").append(new TextComponent("keybind: ").withStyle(ChatFormatting.GOLD));
		Collection<Object> boundKeybinds = filterUnboundKeybinds(keybinds);

		if (boundKeybinds.isEmpty()) {
			return component.append("-");
		}

		int i = 0;

		for (Object o : boundKeybinds) {
			if (i++ > 0) {
				component.append(" OR ");
			}

			if (o instanceof KeyMapping) {
				component.append(formatKeybind((KeyMapping)o));
			} else if (o instanceof Key) {
				component.append(formatKeybind((Key)o));
			} else if (o instanceof Key[]) {
				component.append(formatKeybind((Key[])o));
			} else if (o instanceof Object[]) {
				component.append(formatKeybind((Object[])o));
			}
		}

		return component;
	}

	private static Collection<Object> filterUnboundKeybinds(Object... keybinds) {
		Collection<Object> boundKeybinds = new LinkedList<>();

		for (Object o : keybinds) {
			if (o instanceof KeyMapping) {
				KeyMapping keybind = (KeyMapping)o;

				if (keybind.isUnbound()) {
					continue;
				}
			}

			boundKeybinds.add(o);
		}

		return boundKeybinds;
	}

	public static Component formatKeybind(KeyMapping keybind) {
		Component component = new TextComponent("");

		if (keybind.isUnbound()) {
			return component;
		}

		AmecsHelper.addModifiers(component, keybind);

		Key boundKey = ((IKeyMapping)keybind).rsmm$getKey();
		component.append(formatKey(boundKey));

		return component;
	}

	public static Component formatKeybind(Key... keys) {
		Component component = new TextComponent("");

		for (int i = 0; i < keys.length; i++) {
			Key key = keys[i];

			if (i > 0) {
				component.append(" + ");
			}

			component.append(formatKey(key));
		}

		return component;
	}

	public static Component formatKeybind(Object... keys) {
		List<Component> formattedKeys = new ArrayList<>();

		for (Object o : keys) {
			if (o instanceof KeyMapping) {
				formattedKeys.add(formatKeybind((KeyMapping)o));
			} else if (o instanceof Key) {
				formattedKeys.add(formatKey((Key)o));
			} else if (o instanceof String) {
				formattedKeys.add(formatKey((String)o));
			}
		}

		Component text = new TextComponent("");

		for (int i = 0; i < formattedKeys.size(); i++) {
			Component key = formattedKeys.get(i);

			if (i > 0) {
				text.append(" + ");
			}

			text.append(key);
		}

		return text;
	}

	public static Component formatKey(Key key) {
		int code = key.getValue();
		String translationKey = key.getName();

		String keyName = null;

		switch (key.getType()) {
		case KEYSYM:
			keyName = InputConstants.translateKeyCode(code);
			break;
		case SCANCODE:
			keyName = InputConstants.translateScanCode(code);
			break;
		case MOUSE:
			String buttonName = I18n.get(translationKey);

			if (Objects.equals(translationKey, buttonName)) {
				keyName = I18n.get(InputConstants.Type.MOUSE.getDefaultPrefix(), code + 1);
			} else {
				keyName = buttonName;
			}

			break;
		}
		if (keyName == null) {
			keyName = I18n.get(translationKey);
		}

		return formatKey(keyName);
	}

	public static Component formatKey(String key) {
		return new TextComponent(key).withStyle(ChatFormatting.YELLOW);
	}

	public static Component formatKey(Component key) {
		return key.copy().withStyle(ChatFormatting.YELLOW);
	}
}
