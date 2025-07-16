package redstone.multimeter.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.resources.ResourceManager;

import redstone.multimeter.client.gui.text.BuiltInTranslations;

@Mixin(ClientLanguage.class)
public class ClientLanguageMixin {

	@Inject(
		method = "loadFrom",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
		)
	)
	private static void addBuiltInRsmmTranslations(ResourceManager resourceManager, List<LanguageInfo> langs, CallbackInfoReturnable<ClientLanguage> cir, @Local Map<String, String> storage) {
		BuiltInTranslations.apply(storage, langs);
	}
}
