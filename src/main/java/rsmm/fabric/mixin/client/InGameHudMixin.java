package rsmm.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.interfaces.mixin.IMinecraftClient;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	
	private MultimeterClient client;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInitInjectAtReturn(MinecraftClient minecraftClient, CallbackInfo ci) {
		this.client = ((IMinecraftClient)minecraftClient).getMultimeterClient();
	}
	
	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V"
			)
	)
	private void onRenderInjectAfterRenderStatusEffectOverlay(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		if (client.renderHud()) {
			client.getHudRenderer().render(matrices);
		}
	}
}
