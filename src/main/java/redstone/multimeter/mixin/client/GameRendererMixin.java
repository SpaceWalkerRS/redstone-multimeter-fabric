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
		method = "render(IFJ)V",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/client/render/block/BlockLayer;TRANSLUCENT:Lnet/minecraft/client/render/block/BlockLayer;"
			)
		),
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/render/world/WorldRenderer;render(Lnet/minecraft/client/render/block/BlockLayer;DILnet/minecraft/entity/Entity;)I"
		)
	)
	private void renderMeters(int anaglyphRenderPass, float tickDelta, long timeNanos, CallbackInfo ci) {
		MeterRenderer renderer = MultimeterClient.INSTANCE.getMeterRenderer();

		renderer.renderMeters(tickDelta);
		renderer.renderMeterNameTags(tickDelta);
	}
}
