package redstone.multimeter.client.compat.amecs;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.duck.IKeyBinding;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

import redstone.multimeter.client.gui.text.Formatting;
import redstone.multimeter.client.gui.text.Text;
import redstone.multimeter.client.gui.text.Texts;

public class AmecsHelper {

	private static boolean isAmecsApiLoaded = FabricLoader.getInstance().isModLoaded("amecsapi");

	public static Text addModifiers(Text text, KeyMapping keybind) {
		if (isAmecsApiLoaded) {
			KeyModifiers modifiers = ((IKeyBinding)keybind).amecs$getKeyModifiers();

			if (modifiers.getAlt())
				text.append(Texts.literal("Alt").format(Formatting.YELLOW)).append(" + ");
			if (modifiers.getControl())
				text.append(Texts.literal("Control").format(Formatting.YELLOW)).append(" + ");
			if (modifiers.getShift())
				text.append(Texts.literal("Shift").format(Formatting.YELLOW)).append(" + ");
		}

		return text;
	}
}
