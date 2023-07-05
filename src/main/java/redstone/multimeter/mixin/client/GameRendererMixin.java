package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;

import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(
		method = "render(FJ)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=hand"
		)
	)
	private void renderMeterHighlights(float tickDelta, long timeNanos, CallbackInfo ci) {
		((IMinecraft)minecraft).getMultimeterClient().getMeterRenderer().renderMeters(tickDelta);
	}
}
