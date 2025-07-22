package redstone.multimeter.mixin.client;

import org.joml.Matrix4f;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderBuffers;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.render.MeterRenderer;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow @Final private RenderBuffers renderBuffers;
	@Shadow @Final private LevelTargetBundle targets;

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/world/phys/Vec3;FLnet/minecraft/client/renderer/FogParameters;)V"
		)
	)
	private void renderMeters(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f cameraPose, Matrix4f projectionPose, CallbackInfo ci, @Local(ordinal = 0) FogParameters fogParameters, @Local FrameGraphBuilder builder) {
		FramePass pass = builder.addPass("rsmm:meters");

		targets.main = pass.readsAndWrites(targets.main);
		if (targets.translucent != null) {
			targets.translucent = pass.readsAndWrites(targets.translucent);
		}

		pass.executes(() -> {
			RenderSystem.setShaderFog(fogParameters);

			BufferSource bufferSource = renderBuffers.bufferSource();
			MeterRenderer renderer = MultimeterClient.INSTANCE.getMeterRenderer();

			PoseStack poses = new PoseStack();

			renderer.renderMeters(poses, bufferSource);
			renderer.renderMeterNameTags(poses, bufferSource);

			bufferSource.endBatch();
		});
	}
}
