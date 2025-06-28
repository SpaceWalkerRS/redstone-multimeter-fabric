package redstone.multimeter.client.gui.text;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.RedstoneMultimeterMod;

public class Texts {

	public static final Text MOD_NAME = literal(RedstoneMultimeterMod.MOD_NAME);
	public static final Text GUI_DONE = translatable("gui.done");
	public static final Text GUI_CANCEL = translatable("gui.cancel");
	public static final Text GUI_CONTROLS = translatable("options.controls");
	public static final Text GUI_RESET = translatable("controls.reset");
	public static final Text ADDITION = literal("+");
	public static final Text SUBTRACTION = literal("-");

	public static Text of(Object o) {
		if (o instanceof Text) {
			return (Text) o;
		} else if (o instanceof net.minecraft.text.Text) {
			return resolve((net.minecraft.text.Text) o);
		} else {
			return literal(o.toString());
		}
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

	public static Text guiReset() {
		return GUI_RESET;
	}

	public static Text addition() {
		return ADDITION;
	}

	public static Text subtraction() {
		return SUBTRACTION;
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
			if (o instanceof KeyBinding) {
				KeyBinding keybind = (KeyBinding) o;

				if (keybind.getKeyCode() == Keyboard.KEY_NONE) {
					continue;
				}
			}

			if (i++ > 0) {
				t.append(" OR ");
			}

			if (o instanceof KeyBinding) {
				t.append(keybind((KeyBinding) o));
			} else if (o instanceof Integer) {
				t.append(key((Integer)o));
			} else if (o instanceof Object[]) {
				t.append(keys((Object[])o));
			} else {
				t.append(of(o));
			}
		}

		// no (bound) keybinds
		if (i == 0) {
			t.append("-");
		}

		return t;
	}

	public static Text keys(Object... keys) {
		Text t = literal("");

		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];

			if (i > 0) {
				t.append(" + ");
			}

			if (key instanceof KeyBinding) {
				t.append(keybind((KeyBinding) key));
			} else if (key instanceof Integer) {
				t.append(key((Integer) key));
			} else {
				t.append(of(key).format(Formatting.YELLOW));
			}
		}

		return t;
	}

	public static Text keybind(KeyBinding keybind) {
		return key(keybind.getKeyCode());
	}

	public static Text key(int key) {
		return literal(GameOptions.getKeyName(key)).format(Formatting.YELLOW);
	}

	public static Text resolve(net.minecraft.text.Text component) {
		Text t;

		if (component instanceof net.minecraft.text.LiteralText) {
			t = literal(((net.minecraft.text.LiteralText) component).getRawString());
		} else if (component instanceof net.minecraft.text.TranslatableText) {
			net.minecraft.text.TranslatableText translatable = (net.minecraft.text.TranslatableText) component;

			t = translatable(
				translatable.getKey(),
				translatable.getArgs()
			);
		} else {
			throw new IllegalStateException("cannot convert " + component + " to RSMM Text!");
		}

		t.format(Style.resolve(component.getStyle()));

		for (net.minecraft.text.Text sibling : component.getSiblings()) {
			t.append(resolve(sibling));
		}

		return t;
	}
}
