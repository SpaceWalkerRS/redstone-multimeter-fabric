package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_4218;
import net.minecraft.client.MinecraftClient;

import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(class_4218.class)
public class GameRendererMixin {
	
	@Shadow @Final private MinecraftClient field_20681;
	
	@Inject(
			method = "method_19074",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=hand"
			)
	)
	private void renderMeterHighlights(float tickDelta, long limitTime, CallbackInfo ci) {
		((IMinecraftClient)field_20681).getMultimeterClient().getMeterRenderer().renderMeters(field_20681.getCameraEntity(), tickDelta);
	}
}
