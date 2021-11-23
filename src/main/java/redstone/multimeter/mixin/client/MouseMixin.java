package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(Mouse.class)
public class MouseMixin {
	
	@Shadow @Final private MinecraftClient client;
	@Shadow private double eventDeltaWheel;
	
	@Inject(
			method = "onMouseScroll",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"
			)
	)
	private void scrollOnScreen(long windowHandle, double horizontal, double vertical, CallbackInfo ci, double scrollY, double mouseX, double mouseY) {
		MultimeterClient multimeterClient = ((IMinecraftClient)client).getMultimeterClient();
		RSMMScreen screen = multimeterClient.getScreen();
		
		if (screen != null) {
			boolean discrete = client.options.discreteMouseScroll;
			double sensitivity = client.options.mouseWheelSensitivity;
			double scrollX = sensitivity * (discrete ? Math.signum(horizontal) : horizontal);
			
			screen.mouseScroll(mouseX, mouseY, scrollX, scrollY);
		}
	}
	
	@Inject(
			method = "onMouseScroll",
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"
			)
	)
	private void scrollInGame(long windowHandle, double horizontal, double vertical, CallbackInfo ci, double scrollY, float scrollDeltaY) {
		boolean discrete = client.options.discreteMouseScroll;
		double sensitivity = client.options.mouseWheelSensitivity;
		double scrollX = sensitivity * (discrete ? Math.signum(horizontal) : horizontal);
		
		if (((IMinecraftClient)client).getMultimeterClient().getInputHandler().handleMouseScroll(scrollX, scrollY)) {
			ci.cancel();
		}
	}
}
