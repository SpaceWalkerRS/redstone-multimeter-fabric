package rsmm.fabric.mixin.client;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import rsmm.fabric.client.KeyBindings;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
	
	@Shadow @Final @Mutable private KeyBinding[] keysAll;
	
	@Inject(
			method = "load",
			at = @At(
					value = "HEAD"
			)
	)
	private void onLoadInjectAtHead(CallbackInfo ci) {
		keysAll = ArrayUtils.addAll(keysAll, 
			KeyBindings.TOGGLE_METER,
			KeyBindings.PAUSE_METERS,
			KeyBindings.STEP_FORWARD,
			KeyBindings.STEP_BACKWARD,
			KeyBindings.TOGGLE_HUD,
			KeyBindings.OPEN_MULTIMETER_SCREEN
		);
	}
}
