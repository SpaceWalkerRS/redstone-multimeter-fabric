package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {
	
	private MultimeterClient multimeterClient;
	
	@Inject(
			method = "onResolutionChanged",
			at = @At(
					value = "HEAD"
			)
	)
	private void onResolutionChanged(CallbackInfo ci) {
		getMultimeterClient().getHUD().resetSize();
	}
	
	@Inject(
			method = "handleInputEvents",
			at = @At(
					value = "HEAD"
			)
	)
	private void onHandleInputEventsInjectAtHead(CallbackInfo ci) {
		getMultimeterClient().getInputHandler().handleInputEvents();
	}
	
	@Inject(
			method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onDisconnectInjectAtHead(Screen screen, CallbackInfo ci) {
		getMultimeterClient().onDisconnect();
	}
	
	@Inject(
			method = "close",
			at = @At(
					value = "HEAD"
			)
	)
	private void onCloseInjectAtHead(CallbackInfo ci) {
		getMultimeterClient().onShutdown();
	}
	
	@Override
	public MultimeterClient getMultimeterClient() {
		if (multimeterClient == null) {
			multimeterClient = new MultimeterClient((MinecraftClient)(Object)this);
		}
		
		return multimeterClient;
	}
}
