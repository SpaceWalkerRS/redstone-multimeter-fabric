package redstone.multimeter.client.gui.text;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.mixin.common.TextAccessor;

public class Texts {

	public static final Text MOD_NAME = literal(RedstoneMultimeterMod.MOD_NAME);
	public static final Text GUI_DONE = translatable("gui.done");
	public static final Text GUI_CANCEL = translatable("gui.cancel");
	public static final Text GUI_CONTROLS = translatable("options.controls");
	public static final Text GUI_CONTROLS_RESET = translatable("controls.reset");

	public static final String ACTION_BAR_KEY = "rsmm:action_bar|";

	public static Text of(Object o) {
		if (o instanceof Text) {
			return (Text) o;
		} else if (o instanceof net.minecraft.text.Text) {
			return resolve((net.minecraft.text.Text) o);
		} else if (o instanceof String) {
			return resolve((String) o);
		} else {
			return literal(o.toString());
		}
	}

	public static Text actionBar(Object t) {
		return literal(ACTION_BAR_KEY).append(of(t));
	}

	public static Text literal(String t) {
		return new LiteralText(t);
	}

	public static Text literal(String t, Object... args) {
		return new LiteralText(String.format(t, args));
	}

	public static Text translatable(String t) {
		return new TranslatableText(t);
	}

	public static Text translatable(String t, Object... args) {
		return new TranslatableText(t, args);
	}

	public static Text composite(Object... ts) {
		Text t = literal("");

		for (Object o : ts) {
			t.append(of(o));
		}

		return t;
	}

	public static Text modName() {
		return MOD_NAME;
	}

	public static Text guiDone() {
		return GUI_DONE;
	}

	public static Text guiCancel() {
		return GUI_CANCEL;
	}

	public static Text guiControls() {
		return GUI_CONTROLS;
	}

	public static Text guiControlsReset() {
		return GUI_CONTROLS_RESET;
	}

	public static Text keyValue(Object key, Object value) {
		return composite(
			composite(
				key,
				": "
			).format(Formatting.GOLD),
			value
		);
	}

	public static Text keybinds(Object... keybinds) {
		Text t = literal("");

		int i = 0;

		for (Object o : keybinds) {
			Text keybind;

			if (o instanceof KeyBinding) {
				keybind = keybind(true, (KeyBinding) o);
			} else if (o instanceof Integer) {
				keybind = key((Integer) o);
			} else if (o instanceof Object[]) {
				keybind = keys(true, (Object[]) o);
			} else {
				keybind = key(o);
			}

			if (keybind != null) {
				if (i++ > 0) {
					t.append(" OR ");
				}

				t.append(keybind);
			}
		}

		// no (bound) keybinds
		if (i == 0) {
			t.append(composite(
				"<",
				translatable("rsmm.keybind.unbound"),
				">"
			));
		}

		return t;
	}

	public static Text keys(Object... keys) {
		return keys(false, keys);
	}

	private static Text keys(boolean nullable, Object... keys) {
		Text t = literal("");

		for (int i = 0; i < keys.length; i++) {
			Object o = keys[i];
			Text key;

			if (o instanceof KeyBinding) {
				key = keybind(nullable, (KeyBinding) o);
			} else if (o instanceof Integer) {
				key = key((Integer) o);
			} else {
				key = key(o);
			}

			if (nullable && key == null) {
				return null;
			}

			if (i > 0) {
				t.append(" + ");
			}

			t.append(key);
		}

		return t;
	}

	public static Text keybind(KeyBinding keybind) {
		return keybind(false, keybind);
	}

	private static Text keybind(boolean nullable, KeyBinding keybind) {
		if (keybind.keyCode == Keyboard.KEY_NONE) {
			if (nullable) {
				return null;
			} else {
				return composite(
					"<",
					translatable("rsmm.keybind.unbound"),
					">"
				);
			}
		} else {
			return key(keybind.keyCode);
		}
	}

	public static Text key(int key) {
		return key(GameOptions.getKeyName(key));
	}

	public static Text key(Object name) {
		return of(name).format(Formatting.YELLOW);
	}

	public static Text resolve(net.minecraft.text.Text component) {
		TextAccessor text = (TextAccessor) component;

		Text t;

		if (text.rsmm$getText() != null) {
			t = literal(text.rsmm$getText());
		} else if (text.rsmm$getKey() != null) {
			if (text.rsmm$getUsing() != null) {
				t = translatable(text.rsmm$getKey(), text.rsmm$getUsing().toArray());
			} else {
				t = translatable(text.rsmm$getKey());
			}
		} else {
			t = literal("");
		}

		t.format(Style.resolve(component));

		if (text.rsmm$getKey() == null) { // for translatable components, 'using' is the translation args
			for (Object sibling : text.rsmm$getUsing()) {
				t.append(resolve((net.minecraft.text.Text) sibling));
			}
		}

		return t;
	}

	public static Text resolve(String fs) {
		Text t = literal("");
		Style s = Style.EMPTY;

		int from = 0;
		int to = 0;

		boolean lastWasFormattingPrefix = false;
		boolean lastWasFormatting = false;

		for (; to < fs.length(); to++) {
			char chr = fs.charAt(to);

			boolean thisIsFormattingPrefix = (chr == Formatting.PREFIX);
			boolean thisIsStringEnd = (to == fs.length() - 1);

			if (lastWasFormattingPrefix) {
				Formatting f = Formatting.byCode(chr);

				if (f == Formatting.RESET) {
					s = Style.EMPTY;
				} else {
					s = s.applyFormattings(f);
				}

				// make sure current char is not captured
				if (thisIsStringEnd) {
					from = to;
				}

				lastWasFormattingPrefix = false;
				lastWasFormatting = true;
			} else if (thisIsFormattingPrefix) {
				lastWasFormattingPrefix = true;
				lastWasFormatting = false;
			} else {
				if (lastWasFormatting) {
					from = to;
				}
				// make sure current char is captured
				if (thisIsStringEnd) {
					to++;
				}

				lastWasFormattingPrefix = false;
				lastWasFormatting = false;
			}

			if (thisIsFormattingPrefix || thisIsStringEnd) {
				if (from != to) {
					t.append(literal(fs.substring(from, to)).format(s));
				}

				from = to;
			}
		}

		return t;
	}
}
