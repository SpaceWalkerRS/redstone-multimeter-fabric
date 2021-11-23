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
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(Mouse.class)
public class MouseMixin {
	
	@Shadow @Final private MinecraftClient client;
	@Shadow private double x;
	@Shadow private double y;
	
	@Inject(
			method = "onMouseScroll",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(D)Z"
			)
	)
	private void scrollOnScreen(long windowHandle, double horizontal, double vertical, CallbackInfo ci, double scrollY) {
		MultimeterClient multimeterClient = ((IMinecraftClient)client).getMultimeterClient();
		RSMMScreen screen = multimeterClient.getScreen();
		
		if (screen != null) {
			double sensitivity = client.options.mouseWheelSensitivity;
			double scrollX = sensitivity * vertical;
			
			double mouseX = x * client.window.getScaledWidth() / client.window.getWidth();
			double mouseY = y * client.window.getScaledHeight() / client.window.getHeight();
			
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
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;method_7325()Z"
			)
	)
	private void scrollInGame(long windowHandle, double horizontal, double vertical, CallbackInfo ci, double scrollY, double scrollDeltaY) {
		double sensitivity = client.options.mouseWheelSensitivity;
		double scrollX = sensitivity * vertical;
		
		if (((IMinecraftClient)client).getMultimeterClient().getInputHandler().handleMouseScroll(scrollX, scrollY)) {
			ci.cancel();
		}
	}
	
	@Inject(
			method = "onCursorPos",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
			)
	)
	private void onMouseMoved(long windowHandle, double x, double y, CallbackInfo ci) {
		RSMMScreen screen = ((IMinecraftClient)client).getMultimeterClient().getScreen();
		
		if (screen != null) {
			double mouseX = x * client.window.getScaledWidth() / client.window.getWidth();
            double mouseY = y * client.window.getScaledHeight() / client.window.getHeight();
			
			Screen.method_2217(() -> screen.mouseMove(mouseX, mouseY), "mouseMoved event handler", screen.getClass().getCanonicalName());
		}
	}
}
