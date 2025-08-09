package redstone.multimeter.mixin.client;

import org.joml.Matrix4f;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderBuffers;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.render.MeterRenderer;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow @Final private RenderBuffers renderBuffers;

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(Lnet/minecraft/client/Camera;)V"
		)
	)
	private void renderMeters(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f cameraPose, Matrix4f projectionPose, CallbackInfo ci) {
		BufferSource bufferSource = renderBuffers.bufferSource();
		MeterRenderer renderer = MultimeterClient.INSTANCE.getMeterRenderer();

		PoseStack poses = new PoseStack();

		renderer.renderMeterHighlights(poses, bufferSource);
		renderer.renderMeterNameTags(poses, bufferSource);

		bufferSource.endBatch();
	}
}
