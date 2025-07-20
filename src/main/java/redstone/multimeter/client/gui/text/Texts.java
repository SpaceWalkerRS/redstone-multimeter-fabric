package redstone.multimeter.client.gui.text;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.interfaces.mixin.IKeyBinding;
import redstone.multimeter.mixin.client.InputConstantsTypeAccessor;

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
		} else if (o instanceof String) {
			return resolve((String) o);
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
			Text keybind;

			if (o instanceof KeyBinding) {
				keybind = keybind(true, (KeyBinding) o);
			} else if (o instanceof Key) {
				keybind = key((Key) o);
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
			t.append("<unbound>");
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
			} else if (o instanceof Key) {
				key = key((Key) o);
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
		if (keybind.isUnbound()) {
			if (nullable) {
				return null;
			} else {
				return literal("<unbound>");
			}
		} else {
			return key(((IKeyBinding) keybind).rsmm$getKey());
		}
	}

	public static Text key(Key key) {
		int code = key.getValue();
		String name = key.getName();

		Object displayName = null;

		switch (key.getType()) {
		case KEYSYM:
			displayName = GLFW.glfwGetKeyName(code, -1);
			break;
		case SCANCODE:
			displayName = GLFW.glfwGetKeyName(-1, code);
			break;
		case MOUSE:
			if (!I18n.hasTranslation(name)) {
				displayName = translatable(((InputConstantsTypeAccessor) (Object) InputConstants.Type.MOUSE).rsmm$getDefaultPrefix(), code + 1);
			}

			break;
		}

		return displayName == null ? key(translatable(name)) : key(displayName);
	}

	public static Text key(Object name) {
		return of(name).format(Formatting.YELLOW);
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

	public static Text resolve(String fs) {
		Text t = literal("");
		Style s = Style.EMPTY;

		int start = 0;
		int end = 0;

		boolean parseAsFormatting = false;

		for (; end < fs.length(); end++) {
			char chr = fs.charAt(end);

			if (parseAsFormatting) {
				Formatting f = Formatting.byCode(chr);

				if (f == Formatting.RESET) {
					s = Style.EMPTY;
				} else {
					s = s.applyFormattings(f);
				}

				parseAsFormatting = false;
			} else if (chr == Formatting.PREFIX) {
				t.append(literal(fs.substring(start, end)).format(s));

				start = end;
				parseAsFormatting = true;
			}
		}

		return t;
	}
}
