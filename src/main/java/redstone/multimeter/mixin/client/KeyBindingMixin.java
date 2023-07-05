package redstone.multimeter.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.options.KeyBinding;

import redstone.multimeter.client.Keybinds;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	@Shadow @Final private static Map<String, Integer> CATEGORY_SORT_ORDER;

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "TAIL"
		)
	)
	private static void init(CallbackInfo ci) {
		for (String category : Keybinds.getCategories()) {
			CATEGORY_SORT_ORDER.put(category, CATEGORY_SORT_ORDER.size() + 1);
		}
	}
}
