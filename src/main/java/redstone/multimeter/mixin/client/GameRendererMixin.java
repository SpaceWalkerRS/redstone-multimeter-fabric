package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.GameRenderer;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.render.MeterRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
		method = "render(FJ)V",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/BlockLayer;TRANSLUCENT:Lnet/minecraft/world/level/BlockLayer;"
			)
		),
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/renderer/LevelRenderer;render(Lnet/minecraft/world/level/BlockLayer;Lnet/minecraft/client/Camera;)I"
		)
	)
	private void renderMeters(float partialTick, long timeNanos, CallbackInfo ci) {
		MeterRenderer renderer = MultimeterClient.INSTANCE.getMeterRenderer();

		renderer.renderMeters();
		renderer.renderMeterNameTags();
	}
}
