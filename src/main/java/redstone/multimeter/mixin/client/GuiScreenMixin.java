package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
	
	@Inject(
			method = "setWorldAndResolution",
			at = @At(
					value = "HEAD"
			)
	)
	private void onInit(Minecraft minecraftClient, int width, int height, CallbackInfo ci) {
		MultimeterClient client = ((IMinecraft)minecraftClient).getMultimeterClient();
		
		if (client != null) {
			client.getHUD().resetSize();
		}
	}
}
