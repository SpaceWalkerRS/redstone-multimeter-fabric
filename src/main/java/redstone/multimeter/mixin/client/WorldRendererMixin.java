package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;

import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(
			method = "renderWorldBorder",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRenderInjectBeforeRenderParticles(Camera camera, float delta, CallbackInfo ci) {
		((IMinecraftClient)client).getMultimeterClient().getMeterRenderer().renderMeters();
	}
}
