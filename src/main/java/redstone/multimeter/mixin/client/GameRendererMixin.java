package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.GameRenderer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.render.MeterRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
		method = "renderWorld(FJ)V",
		slice = @Slice(
			from = @At(
				value = "INVOKE_STRING",
				target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
				args = "ldc=water"
			)
		),
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/render/world/WorldRenderer;render(Lnet/minecraft/entity/living/LivingEntity;ID)I"
		)
	)
	private void renderMeters(float tickDelta, long timeNanos, CallbackInfo ci) {
		MeterRenderer renderer = MultimeterClient.INSTANCE.getMeterRenderer();

		renderer.renderMeters(tickDelta);
		renderer.renderMeterNameTags(tickDelta);
	}
}
