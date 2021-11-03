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
	
	@Shadow @Final private static Map<String, Integer> CATEGORY_ORDER_MAP;
	
	@Inject(
			method = "<clinit>",
			at = @At(
					value = "RETURN"
			)
	)
	private static void initKeybinds(CallbackInfo ci) {
		for (String category : KeyBindings.getCategories()) {
			CATEGORY_ORDER_MAP.put(category, CATEGORY_ORDER_MAP.size() + 1);
		}
	}
}
