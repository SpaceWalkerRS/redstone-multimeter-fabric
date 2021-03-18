package rsmm.fabric.mixin.client;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.options.GameOptions;

import rsmm.fabric.client.KeyBindings;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
	
	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lorg/apache/commons/lang3/ArrayUtils;addAll([Ljava/lang/Object;[Ljava/lang/Object;)[Ljava/lang/Object;"
			)
	)
	private Object[] onInitInjectAtReturn(Object[] keyBindings, Object[] hotbarKeyBindings) {
		Object[] rsmmKeyBindings = new Object[] {
			KeyBindings.TOGGLE_METER,
			KeyBindings.PAUSE_METERS,
			KeyBindings.STEP_FORWARD,
			KeyBindings.STEP_BACKWARD,
			KeyBindings.TOGGLE_HUD,
			KeyBindings.PRINT
		};
		
		return ArrayUtils.addAll(ArrayUtils.addAll(keyBindings, hotbarKeyBindings), rsmmKeyBindings);
	}
}
