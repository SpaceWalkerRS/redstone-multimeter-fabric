package rsmm.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.interfaces.mixin.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {
	
	private MultimeterClient multimeterClient;
	
	@Inject(
			method = "init",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInitInjectBeforeIsMultiplayerEnabled(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((MinecraftClient)(Object)this);
		
		multimeterClient.onStartup();
	}
	
	@Inject(
			method = "handleInputEvents",
			at = @At(
					value = "HEAD"
			)
	)
	private void onHandleInputEventsInjectAtHead(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleInputEvents();
	}
	
	@Inject(
			method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onDisconnectInjectAtHead(Screen screen, CallbackInfo ci) {
		multimeterClient.onDisconnect();
	}
	
	@Inject(
			method = "close",
			at = @At(
					value = "HEAD"
			)
	)
	private void onCloseInjectAtHead(CallbackInfo ci) {
		multimeterClient.onShutdown();
	}
	
	@Override
	public MultimeterClient getMultimeterClient() {
		return multimeterClient;
	}
}
