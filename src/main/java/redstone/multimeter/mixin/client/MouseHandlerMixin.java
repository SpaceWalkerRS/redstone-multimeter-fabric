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

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.screen.RSMMScreen;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(
		method = "onScroll",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z"
		)
	)
	private void scrollOnScreen(long window, double horizontal, double vertical, CallbackInfo ci, double scrollY, double mouseX, double mouseY) {
		MultimeterClient multimeterClient = ((IMinecraft)minecraft).getMultimeterClient();
		RSMMScreen screen = multimeterClient.getScreen();
		
		if (screen != null) {
			boolean discrete = minecraft.options.discreteMouseScroll;
			double sensitivity = minecraft.options.mouseWheelSensitivity;
			double scrollX = sensitivity * (discrete ? Math.signum(horizontal) : horizontal);
			
			screen.mouseScroll(mouseX, mouseY, scrollX, scrollY);
		}
	}

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
	private void scrollInGame(long window, double horizontal, double vertical, CallbackInfo ci, double scrollY, float scrollDeltaY) {
		boolean discrete = minecraft.options.discreteMouseScroll;
		double sensitivity = minecraft.options.mouseWheelSensitivity;
		double scrollX = sensitivity * (discrete ? Math.signum(horizontal) : horizontal);
		
		if (((IMinecraft)minecraft).getMultimeterClient().getInputHandler().handleMouseScroll(scrollX, scrollY)) {
			ci.cancel();
		}
	}
}
