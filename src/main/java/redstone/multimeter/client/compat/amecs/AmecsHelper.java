package redstone.multimeter.client.compat.amecs;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.ModifierPrefixTextProvider.Variation;
import de.siphalor.amecs.impl.duck.IKeyBinding;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.Text;

public class AmecsHelper {

	public static Collection<KeyModifier> getKeyModifiers(KeyBinding keybind) {
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
		return variation.getTranslatableText(modifier.getTranslationKey());
	}

	public static Text getModifierName(KeyModifier modifier) {
		return getModifierName(modifier, Variation.NORMAL);
	}
}
