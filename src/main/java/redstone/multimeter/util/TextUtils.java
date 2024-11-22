package redstone.multimeter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.text.Formatting;

import redstone.multimeter.client.gui.Tooltip;

public class TextUtils {

	public static final String ACTION_BAR_KEY = "rsmm:action_bar|";

	private static final int MAX_WIDTH = 200;

	public static List<String> toLines(TextRenderer textRenderer, String text) {
		List<String> lines = new ArrayList<>();

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

				if (textRenderer.getWidth(subString) > MAX_WIDTH) {
					if (lastSpace >= 0) {
						subString = text.substring(0, lastSpace);
						length = lastSpace + 1;
					}

					String line = subString;
					lines.add(line);

					break;
				}
			}

			if (length == text.length()) {
				if (length > 0) {
					String line = text;
					lines.add(line);
				}

				break;
			}

			text = text.substring(length);
		}

		return lines;
	}

	public static void formatKeyValue(List<String> lines, String key, Object value) {
		formatKeyValue(lines, key, value.toString());
	}

	public static void formatKeyValue(List<String> lines, String key, String value) {
		lines.add(formatKeyValue(key, value));
	}

	public static void formatKeyValue(Tooltip tooltip, String key, Object value) {
		formatKeyValue(tooltip, key, value.toString());
	}

	public static void formatKeyValue(Tooltip tooltip, String key, String value) {
		tooltip.add(formatKeyValue(key, value));
	}

	public static String formatKeyValue(String key, Object value) {
		return Formatting.GOLD + key + ": " + Formatting.RESET + value.toString();
	}

	public static String formatKeybindInfo(Object... keybinds) {
		String component = Formatting.GOLD + "keybind: ";
		Collection<Object> boundKeybinds = filterUnboundKeybinds(keybinds);

		if (boundKeybinds.isEmpty()) {
			return component + Formatting.RESET + "-";
		}

		int i = 0;

		for (Object o : boundKeybinds) {
			component += Formatting.RESET;

			if (i++ > 0) {
				component += " OR ";
			}

			if (o instanceof KeyBinding) {
				component += formatKeybind((KeyBinding)o);
			} else if (o instanceof Integer) {
				component += formatKeybind((int)o);
			} else if (o instanceof Integer[]) {
				component += formatKeybind((int[])o);
			} else if (o instanceof Object[]) {
				component += formatKeybind((Object[])o);
			}
		}

		return component;
	}

	private static Collection<Object> filterUnboundKeybinds(Object... keybinds) {
		Collection<Object> boundKeybinds = new LinkedList<>();

		for (Object o : keybinds) {
			if (o instanceof KeyBinding) {
				KeyBinding keybind = (KeyBinding)o;

				if (keybind.keyCode == Keyboard.KEY_NONE) {
					continue;
				}
			}

			boundKeybinds.add(o);
		}

		return boundKeybinds;
	}

	public static String formatKeybind(KeyBinding keybind) {
		String component = "";
		int boundKey = keybind.keyCode;

		if (boundKey == Keyboard.KEY_NONE) {
			return component;
		}

		component += formatKey(boundKey);

		return component;
	}

	public static String formatKeybind(Integer... keys) {
		String component = "";

		for (int i = 0; i < keys.length; i++) {
			int key = keys[i];

			if (i > 0) {
				component += Formatting.RESET + " + ";
			}

			component += formatKey(key);
		}

		return component;
	}

	public static String formatKeybind(Object... keys) {
		List<String> formattedKeys = new ArrayList<>();

		for (Object o : keys) {
			if (o instanceof KeyBinding) {
				formattedKeys.add(formatKeybind((KeyBinding)o));
			} else if (o instanceof Integer) {
				formattedKeys.add(formatKey((int)o));
			} else if (o instanceof String) {
				formattedKeys.add(formatKey((String)o));
			}
		}

		String text = "";

		for (int i = 0; i < formattedKeys.size(); i++) {
			String key = formattedKeys.get(i);

			if (i > 0) {
				text += Formatting.RESET + " + ";
			}

			text += key;
		}

		return text;
	}

	public static String formatKey(int key) {
		return formatKey(GameOptions.getKeyName(key));
	}

	public static String formatKey(String key) {
		return Formatting.YELLOW + key;
	}
}
