package redstone.multimeter.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.option.KeyBinding;

import redstone.multimeter.client.KeyBindings;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {
	
	@Shadow @Final private static Map<String, Integer> categoryOrderMap;
	
	@Inject(
			method = "<clinit>",
			at = @At(
					value = "RETURN"
			)
	)
	private static void onInitInjectAtReturn(CallbackInfo ci) {
		for (String category : KeyBindings.getCategories()) {
			categoryOrderMap.put(category, categoryOrderMap.size() + 1);
		}
	}
}
