package redstone.multimeter.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.KeyCode;

import redstone.multimeter.client.KeyBindings;
import redstone.multimeter.interfaces.mixin.IKeyBinding;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements IKeyBinding {
	
	@Shadow @Final private static Map<String, Integer> categoryOrderMap;
	@Shadow private KeyCode keyCode;
	
	@Inject(
			method = "<clinit>",
			at = @At(
					value = "RETURN"
			)
	)
	private static void initKeybinds(CallbackInfo ci) {
		for (String category : KeyBindings.getCategories()) {
			categoryOrderMap.put(category, categoryOrderMap.size() + 1);
		}
	}
	
	@Override
	public KeyCode getBoundKeyRSMM() {
		return keyCode;
	}
}
