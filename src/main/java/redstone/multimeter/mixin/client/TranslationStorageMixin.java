package redstone.multimeter.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.resource.manager.ResourceManager;

import redstone.multimeter.client.gui.text.BuiltInTranslations;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {

	@Shadow @Final
	private Map<String, String> translations;

	@Inject(
		method = "load",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
		)
	)
	private void addBuiltInRsmmTranslations(ResourceManager resourceManager, List<String> langs, CallbackInfo ci) {
		BuiltInTranslations.apply(translations, langs);
	}
}
