package redstone.multimeter.mixin.common;

import java.util.Locale;
import java.util.Properties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.locale.LanguageManager;

import redstone.multimeter.client.gui.text.BuiltInTranslations;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {

	@Inject(
		method = "loadTranslations",
		at = @At(
			value = "HEAD"
		)
	)
	private void addBuiltInRsmmTranslations(Properties translations, String language, CallbackInfo ci) {
		BuiltInTranslations.apply(translations, language);
		BuiltInTranslations.apply(translations, language.toLowerCase(Locale.ROOT));
	}
}
