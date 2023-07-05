package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Window.class)
public class WindowMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(
		method = "resizeDisplay",
		at = @At(
			value = "FIELD",
			shift = Shift.AFTER,
			ordinal = 0,
			target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screen/Screen;"
		)
	)
	private void resizeDisplay(CallbackInfo ci) {
		MultimeterClient client = ((IMinecraft)minecraft).getMultimeterClient();

		if (client != null) {
			client.getHud().resetSize();
		}
	}
}
