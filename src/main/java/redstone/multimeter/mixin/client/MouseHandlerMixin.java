package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.screen.ScreenWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Shadow @Final private Minecraft minecraft;

	@Shadow private double xpos;
	@Shadow private double ypos;

	@Inject(
		method = "onScroll",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(D)Z"
		)
	)
	private void scrollOnScreen(long window, double horizontal, double vertical, CallbackInfo ci) {
		Screen screen = MultimeterClient.MINECRAFT.screen;
		
		if (screen instanceof ScreenWrapper) {
			double mouseX = xpos * minecraft.window.getGuiScaledWidth() / minecraft.window.getScreenWidth();
			double mouseY = ypos * minecraft.window.getGuiScaledHeight() / minecraft.window.getScreenHeight();
			double discrete = minecraft.options.discreteMouseScroll;
			double scrollX = discrete * horizontal;
			double scrollY = discrete * vertical;
			
			((ScreenWrapper) screen).mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
	}

	@Inject(
		method = "onScroll",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;isSpectator()Z"
		)
	)
	private void scrollInGame(long window, double horizontal, double vertical, CallbackInfo ci) {
		MultimeterClient client = ((IMinecraft)minecraft).getMultimeterClient();

		if (client == null) {
			return;
		}

		double discrete = minecraft.options.discreteMouseScroll;
		double scrollX = discrete * horizontal;
		double scrollY = discrete * vertical;

		if (client.getInputHandler().handleMouseScroll(scrollX, scrollY)) {
			ci.cancel();
		}
	}

	@Inject(
		method = "onMove",
		at = @At(
			value = "FIELD",
			ordinal = 0,
			target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screen/Screen;"
		)
	)
	private void onMove(long window, double horizontal, double vertical, CallbackInfo ci) {
		Screen screen = minecraft.screen;

		if (screen instanceof ScreenWrapper) {
			double mouseX = xpos * minecraft.window.getGuiScaledWidth() / minecraft.window.getScreenWidth();
			double mouseY = ypos * minecraft.window.getGuiScaledHeight() / minecraft.window.getScreenHeight();

			Screen.wrapScreenError(() -> ((ScreenWrapper) screen).mouseMoved(mouseX, mouseY), "mouseMoved event handler", screen.getClass().getCanonicalName());
		}
	}
}
