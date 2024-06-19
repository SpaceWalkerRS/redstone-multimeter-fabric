package redstone.multimeter.mixin.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;

import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/FogRenderer;setupNoFog()V"
		)
	)
	private void renderMeterHighlights(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f cameraPose, Matrix4f projectionPose, CallbackInfo ci) {
		PoseStack poses = new PoseStack();
		poses.mulPose(cameraPose);

		((IMinecraft)minecraft).getMultimeterClient().getMeterRenderer().renderMeters(poses);
	}
}
