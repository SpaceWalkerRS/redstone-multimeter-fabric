package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;

import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private RenderBuffers renderBuffers;

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/client/renderer/FogRenderer;setupNoFog()V"
		)
	)
	private void renderMeterHighlights(PoseStack poses, float partialTick, long timeNanos, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionPose, CallbackInfo ci) {
		((IMinecraft)minecraft).getMultimeterClient().getMeterRenderer().renderMeters(poses);
	}

	@Inject(
		method = "renderLevel",
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			shift = Shift.AFTER,
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;popMatrix()V"
		)
	)
	private void renderMeterNames(PoseStack poses, float partialTick, long timeNanos, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionPose, CallbackInfo ci, @Local MultiBufferSource.BufferSource bufferSource) {
		((IMinecraft)minecraft).getMultimeterClient().getMeterRenderer().renderMeterNames(poses, bufferSource);
	}
}
