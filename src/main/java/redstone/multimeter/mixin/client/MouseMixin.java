package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.class_4112;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.element.RSMMScreen;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(class_4112.class)
public class MouseMixin {
	
	@Shadow @Final private MinecraftClient field_19955;
	@Shadow private double field_19959; // x
	@Shadow private double field_19960; // y
	
	@Inject(
			method = "method_18241",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(D)Z"
			)
	)
	private void scrollOnScreen(long windowHandle, double horizontal, double vertical, CallbackInfo ci, double scrollY) {
		MultimeterClient multimeterClient = ((IMinecraftClient)field_19955).getMultimeterClient();
		RSMMScreen screen = multimeterClient.getScreen();
		
		if (screen != null) {
			double sensitivity = field_19955.options.field_19980;
			double scrollX = sensitivity * vertical;
			
			double mouseX = field_19959 * field_19955.field_19944.method_18321() / field_19955.field_19944.method_18319();
			double mouseY = field_19960 * field_19955.field_19944.method_18322() / field_19955.field_19944.method_18320();
			
			screen.mouseScroll(mouseX, mouseY, scrollX, scrollY);
		}
	}
	
	@Inject(
			method = "method_18241",
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"
			)
	)
	private void scrollInGame(long windowHandle, double horizontal, double vertical, CallbackInfo ci, double scrollY, double scrollDeltaY) {
		double sensitivity = field_19955.options.field_19980;
		double scrollX = sensitivity * vertical;
		
		if (((IMinecraftClient)field_19955).getMultimeterClient().getInputHandler().handleMouseScroll(scrollX, scrollY)) {
			ci.cancel();
		}
	}
	
	@Inject(
			method = "method_18246",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
			)
	)
	private void onMouseMoved(long windowHandle, double x, double y, CallbackInfo ci) {
		MultimeterClient client = ((IMinecraftClient)field_19955).getMultimeterClient();
		
		if (client == null) {
			return;
		}
		
		RSMMScreen screen = client.getScreen();
		
		if (screen == null) {
			return;
		}
		
		double mouseX = x * field_19955.field_19944.method_18321() / field_19955.field_19944.method_18319();
		double mouseY = y * field_19955.field_19944.method_18322() / field_19955.field_19944.method_18320();
		
		Screen.method_18605(() -> screen.mouseMove(mouseX, mouseY), "mouseMoved event handler", screen.getClass().getCanonicalName());
	}
}
