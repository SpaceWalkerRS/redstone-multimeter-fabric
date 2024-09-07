package redstone.multimeter.client.compat.amecs;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.duck.IKeyBinding;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.MutableComponent;

import redstone.multimeter.util.TextUtils;

public class AmecsHelper {

	private static boolean isAmecsApiLoaded = FabricLoader.getInstance().isModLoaded("amecsapi");

	public static MutableComponent addModifiers(MutableComponent text, KeyMapping keybind) {
		if (isAmecsApiLoaded) {
			KeyModifiers modifiers = ((IKeyBinding)keybind).amecs$getKeyModifiers();

			if (modifiers.getAlt())
				text.append(TextUtils.formatKey("Alt")).append(" + ");
			if (modifiers.getControl())
				text.append(TextUtils.formatKey("Control")).append(" + ");
			if (modifiers.getShift())
				text.append(TextUtils.formatKey("Shift")).append(" + ");
		}

		return text;
	}
}
