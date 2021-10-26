package redstone.multimeter.mixin.client;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {
	
	private MultimeterClient multimeterClient;
	
	@Inject(
			method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;",
			at = @At(
					value = "HEAD"
			)
	)
	private void reloadResources(boolean force, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		getMultimeterClient().reloadResources();
	}
	
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
	private void handleInputEvents(CallbackInfo ci) {
		getMultimeterClient().getInputHandler().handleInputEvents();
	}
	
	@Inject(
			method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onDisconnect(Screen screen, CallbackInfo ci) {
		getMultimeterClient().onDisconnect();
	}
	
	@Inject(
			method = "close",
			at = @At(
					value = "HEAD"
			)
	)
	private void onClose(CallbackInfo ci) {
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
