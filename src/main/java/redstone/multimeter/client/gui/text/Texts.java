package redstone.multimeter.client.gui.text;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;

import redstone.multimeter.RedstoneMultimeterMod;
import redstone.multimeter.interfaces.mixin.IKeyMapping;

public class Texts {

	public static final Text MOD_NAME = literal(RedstoneMultimeterMod.MOD_NAME);
	public static final Text GUI_DONE = of(CommonComponents.GUI_DONE);
	public static final Text GUI_CANCEL = of(CommonComponents.GUI_CANCEL);
	public static final Text GUI_CONTROLS = translatable("options.controls");
	public static final Text GUI_RESET = translatable("controls.reset");
	public static final Text ADDITION = literal("+");
	public static final Text SUBTRACTION = literal("-");

	public static Text of(Object o) {
		if (o instanceof Text) {
			return (Text) o;
		} else if (o instanceof Component) {
			return resolve((Component) o);
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
			if (o instanceof KeyMapping) {
				KeyMapping keybind = (KeyMapping) o;

				if (keybind.isUnbound()) {
					continue;
				}
			}

			if (i++ > 0) {
				t.append(" OR ");
			}

			if (o instanceof KeyMapping) {
				t.append(keybind((KeyMapping) o));
			} else if (o instanceof Key) {
				t.append(key((Key)o));
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

			if (key instanceof KeyMapping) {
				t.append(keybind((KeyMapping) key));
			} else if (key instanceof Key) {
				t.append(key((Key) key));
			} else {
				t.append(of(key).format(Formatting.YELLOW));
			}
		}

		return t;
	}

	public static Text keybind(KeyMapping keybind) {
		return key(((IKeyMapping)keybind).rsmm$getKey());
	}

	public static Text key(Key key) {
		return of(key.getDisplayName()).format(Formatting.YELLOW);
	}

	public static Text resolve(Component component) {
		ComponentContents contents = component.getContents();

		Text t;

		if (contents instanceof LiteralContents) {
			t = literal(((LiteralContents) contents).text());
		} else if (contents instanceof TranslatableContents) {
			TranslatableContents translatable = (TranslatableContents) contents;

			t = translatable(
				translatable.getKey(),
				translatable.getArgs()
			);
		} else {
			throw new IllegalStateException("cannot convert " + contents + " to RSMM Text!");
		}

		t.format(Style.resolve(component.getStyle()));

		for (Component sibling : component.getSiblings()) {
			t.append(resolve(sibling));
		}

		return t;
	}
}
