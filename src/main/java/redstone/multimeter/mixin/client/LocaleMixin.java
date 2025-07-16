package redstone.multimeter.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.language.Locale;
import net.minecraft.server.packs.resources.ResourceManager;

import redstone.multimeter.client.gui.text.BuiltInTranslations;

@Mixin(Locale.class)
public class LocaleMixin {

	@Shadow @Final
	private Map<String, String> storage;

	@Inject(
		method = "loadFrom",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
		)
	)
	private void addBuiltInRsmmTranslations(ResourceManager resourceManager, List<String> langs, CallbackInfo ci) {
		BuiltInTranslations.apply(storage, langs);
	}
}
