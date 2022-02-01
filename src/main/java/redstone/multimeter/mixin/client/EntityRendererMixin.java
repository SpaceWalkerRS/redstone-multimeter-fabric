package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	
	@Shadow @Final private Minecraft mc;
	
	@Inject(
			method = "renderWorldPass",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
					args = "ldc=hand"
			)
	)
	private void renderMeters(int pass, float tickDelta, long finishTimeNano, CallbackInfo ci) {
		((IMinecraft)mc).getMultimeterClient().getMeterRenderer().renderMeters(mc.getRenderViewEntity(), tickDelta);
	}
}
