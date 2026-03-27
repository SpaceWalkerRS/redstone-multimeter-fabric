package redstone.multimeter.client.compat.amecs;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiersApi;
import de.siphalor.amecs.key_modifiers.impl.ModifierPrefixTextProvider.Variation;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.KeyMapping;

import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public class AmecsHelper {

	private static boolean isAmecsApiLoaded = FabricLoader.getInstance().isModLoaded("amecsapi");

	public static Collection<AmecsKeyModifier> getKeyModifiers(KeyMapping keybind) {
		AmecsKeyModifierCombination keyModifiers = AmecsKeyModifiersApi.getBoundModifiers(keybind);

		if (keyModifiers.isUnset()) {
			return Collections.emptyList();
		}

		Collection<AmecsKeyModifier> modifiers = new LinkedList<>();

		for (AmecsKeyModifier modifier : AmecsKeyModifiers.getAll()) {
			if (keyModifiers.get(modifier)) {
				modifiers.add(modifier);
			}
		}

		return modifiers;
	}

	public static Text getModifierName(AmecsKeyModifier modifier, Variation variation) {
		return Texts.key(variation.getTranslatableComponent(modifier.getTranslationKey()));
	}

	public static Text getModifierName(AmecsKeyModifier modifier) {
		return getModifierName(modifier, Variation.NORMAL);
	}

	public static Text addModifiers(Text text, KeyMapping keybind) {
		if (isAmecsApiLoaded) {
			Text t = Texts.literal("");

			for (AmecsKeyModifier modifier : getKeyModifiers(keybind)) {
				t.
					append(getModifierName(modifier)).
					append(" + ");
			}

			text = t.append(text);
		}

		return text;
	}
}
