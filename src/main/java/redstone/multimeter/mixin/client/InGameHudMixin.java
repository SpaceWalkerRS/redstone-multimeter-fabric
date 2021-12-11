package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(
			method = "render",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z"
			)
	)
	private void renderHUD(float tickDelta, CallbackInfo ci) {
		MultimeterClient multimeterClient = ((IMinecraftClient)client).getMultimeterClient();
		MultimeterHud hud = multimeterClient.getHUD();
		
		if (multimeterClient.isHudActive() && !hud.isOnScreen()) {
			hud.render();
		}
	}
}
