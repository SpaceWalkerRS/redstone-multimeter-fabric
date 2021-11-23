package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(
			method = "method_3243",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRenderInjectBeforeRenderParticles(Entity camera, float delta, CallbackInfo ci) {
		((IMinecraftClient)client).getMultimeterClient().getMeterRenderer().renderMeters();
	}
}
