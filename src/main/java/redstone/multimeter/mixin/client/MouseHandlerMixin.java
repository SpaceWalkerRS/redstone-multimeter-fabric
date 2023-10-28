package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(
		method = "onScroll",
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true,
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"
		)
	)
	private void scrollInGame(long window, double horizontal, double vertical, CallbackInfo ci, boolean discrete, double sensitivity, double scrollX, double scrollY) {
		if (((IMinecraft)minecraft).getMultimeterClient().getInputHandler().handleMouseScroll(scrollX, scrollY)) {
			ci.cancel();
		}
	}
}
