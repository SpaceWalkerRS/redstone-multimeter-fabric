package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(Screen.class)
public class ScreenMixin {
	
	@Inject(
			method = "init(Lnet/minecraft/client/MinecraftClient;II)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onInit(MinecraftClient minecraftClient, int width, int height, CallbackInfo ci) {
		MultimeterClient client = ((IMinecraftClient)minecraftClient).getMultimeterClient();
		
		if (client != null) {
			client.getHUD().resetSize();
		}
	}
}
