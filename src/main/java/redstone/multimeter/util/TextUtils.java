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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import redstone.multimeter.client.gui.Tooltip;

public class TextUtils {

	public static final String ACTION_BAR_KEY = "rsmm:action_bar|";

	private static final int MAX_WIDTH = 200;

	public static List<Text> toLines(TextRenderer textRenderer, String text) {
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

				if (textRenderer.getWidth(subString) > MAX_WIDTH) {
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
		return new LiteralText("").append(new LiteralText(key + ": ").setFormatting(Formatting.GOLD))
			.append(new LiteralText(value.toString()));
	}

	public static Text formatKeybindInfo(Object... keybinds) {
		Text component = new LiteralText("").append(new LiteralText("keybind: ").setFormatting(Formatting.GOLD));
		Collection<Object> boundKeybinds = filterUnboundKeybinds(keybinds);

		if (boundKeybinds.isEmpty()) {
			return component.append("-");
		}

		int i = 0;

		for (Object o : boundKeybinds) {
			if (i++ > 0) {
				component.append(" OR ");
			}

			if (o instanceof KeyBinding) {
				component.append(formatKeybind((KeyBinding)o));
			} else if (o instanceof Integer) {
				component.append(formatKeybind((int)o));
			} else if (o instanceof Integer[]) {
				component.append(formatKeybind((int[])o));
			} else if (o instanceof Object[]) {
				component.append(formatKeybind((Object[])o));
			}
		}

		return component;
	}

	private static Collection<Object> filterUnboundKeybinds(Object... keybinds) {
		Collection<Object> boundKeybinds = new LinkedList<>();

		for (Object o : keybinds) {
			if (o instanceof KeyBinding) {
				KeyBinding keybind = (KeyBinding)o;

				if (keybind.getKeyCode() == Keyboard.KEY_NONE) {
					continue;
				}
			}

			boundKeybinds.add(o);
		}

		return boundKeybinds;
	}

	public static Text formatKeybind(KeyBinding keybind) {
		Text component = new LiteralText("");
		int boundKey = keybind.getKeyCode();

		if (boundKey == Keyboard.KEY_NONE) {
			return component;
		}

		component.append(formatKey(boundKey));

		return component;
	}

	public static Text formatKeybind(Integer... keys) {
		Text component = new LiteralText("");

		for (int i = 0; i < keys.length; i++) {
			int key = keys[i];

			if (i > 0) {
				component.append(" + ");
			}

			component.append(formatKey(key));
		}

		return component;
	}

	public static Text formatKeybind(Object... keys) {
		List<Text> formattedKeys = new ArrayList<>();

		for (Object o : keys) {
			if (o instanceof KeyBinding) {
				formattedKeys.add(formatKeybind((KeyBinding)o));
			} else if (o instanceof Integer) {
				formattedKeys.add(formatKey((int)o));
			} else if (o instanceof String) {
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

	public static Text formatKey(int key) {
		return formatKey(GameOptions.getKeyName(key));
	}

	public static Text formatKey(String key) {
		return new LiteralText(key).setFormatting(Formatting.YELLOW);
	}

	public static Text formatKey(Text key) {
		return key.copy().setFormatting(Formatting.YELLOW);
	}
}
