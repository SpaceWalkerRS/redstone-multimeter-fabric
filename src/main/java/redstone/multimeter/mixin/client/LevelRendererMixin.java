package redstone.multimeter.mixin.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;

import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private RenderBuffers renderBuffers;

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFog(Lnet/minecraft/client/renderer/FogParameters;)V"
		)
	)
	private void renderMeterHighlights(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f cameraPose, Matrix4f projectionPose, CallbackInfo ci) {
		((IMinecraft)minecraft).getMultimeterClient().getMeterRenderer().renderMeters(cameraPose);
	}

	@Inject(
		method = "method_62214", /* lambda in addMainPass */
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/culling/Frustum;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"
		)
	)
	private void renderMeterNames(FogParameters fogParameters, DeltaTracker deltaTracker, Camera camera, ProfilerFiller profiler, Matrix4f cameraPose, Matrix4f projectionPose, ResourceHandle<RenderTarget> rh1, ResourceHandle<RenderTarget> rh2, ResourceHandle<RenderTarget> rh3, ResourceHandle<RenderTarget> rh4, boolean renderBlockOutline, Frustum frustum, ResourceHandle<RenderTarget> rh5, CallbackInfo ci, @Local(ordinal = 0) MultiBufferSource.BufferSource bufferSource) {
		((IMinecraft)minecraft).getMultimeterClient().getMeterRenderer().renderMeterNames(cameraPose, bufferSource);
	}
}
