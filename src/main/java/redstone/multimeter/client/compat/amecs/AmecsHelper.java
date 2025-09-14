package redstone.multimeter.client.compat.amecs;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.ModifierPrefixTextProvider.Variation;
import de.siphalor.amecs.impl.duck.IKeyBinding;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.KeyMapping;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public class AmecsHelper {

	private static boolean isAmecsApiLoaded = FabricLoader.getInstance().isModLoaded("amecsapi");

	public static Collection<KeyModifier> getKeyModifiers(KeyMapping keybind) {
		KeyModifiers keyModifiers = ((IKeyBinding)keybind).amecs$getKeyModifiers();

		if (keyModifiers.isUnset()) {
			return Collections.emptyList();
		}

		Collection<KeyModifier> modifiers = new LinkedList<>();

		for (KeyModifier modifier : KeyModifier.VALUES) {
			if (modifier != KeyModifier.NONE && keyModifiers.get(modifier)) {
				modifiers.add(modifier);
			}
		}

		return modifiers;
	}

	public static Text getModifierName(KeyModifier modifier, Variation variation) {
		return Texts.key(variation.getTranslation(modifier.getTranslationKey()));
	}

	public static Text getModifierName(KeyModifier modifier) {
		return getModifierName(modifier, Variation.NORMAL);
	}

	public static Text addModifiers(Text text, KeyMapping keybind) {
		if (isAmecsApiLoaded) {
			Text t = Texts.literal("");

			for (KeyModifier modifier : getKeyModifiers(keybind)) {
				t.
					append(getModifierName(modifier)).
					append(" + ");
			}

			text = t.append(text);
		}

		return text;
	}
}