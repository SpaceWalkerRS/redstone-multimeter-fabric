package redstone.multimeter.mixin.client;

import org.joml.Vector4f;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.state.level.CameraRenderState;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.render.MeterRenderer;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow @Final private SubmitNodeStorage submitNodeStorage;
	@Shadow @Final private LevelTargetBundle targets;

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/renderer/LevelRenderer;addMainPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Z)V"
		)
	)
	private void renderMeters(GraphicsResourceAllocator allocator, boolean renderOutline, CameraRenderState cameraState, GpuBufferSlice fogParameters, Vector4f fogColor, boolean renderSky, boolean consistentDepthRequired, CallbackInfo ci, @Local FrameGraphBuilder builder) {
		FramePass pass = builder.addPass("rsmm:meters");

		targets.main = pass.readsAndWrites(targets.main);

		pass.executes(() -> {
			RenderSystem.setShaderFog(fogParameters);

			SubmitNodeCollector collector = this.submitNodeStorage;
			MeterRenderer renderer = MultimeterClient.INSTANCE.getMeterRenderer();

			PoseStack poses = new PoseStack();

			renderer.renderMeterHighlights(poses, collector);
			renderer.renderMeterNameTags(poses, collector);
		});
	}
}
