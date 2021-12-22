package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_4117;
import net.minecraft.client.MinecraftClient;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(class_4117.class)
public class WindowMixin {
	
	@Shadow @Final private MinecraftClient field_20028;
	
	@Inject(
			method = "method_18314",
			at = @At(
					value = "FIELD",
					ordinal = 0,
					target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
			)
	)
	private void onResolutionChanged(CallbackInfo ci) {
		MultimeterClient client = ((IMinecraftClient)field_20028).getMultimeterClient();
		
		if (client != null) {
			client.getHUD().resetSize();
		}
	}
}
