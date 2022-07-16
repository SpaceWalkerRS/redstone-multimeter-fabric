package redstone.multimeter.mixin.client;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {
	
	private MultimeterClient multimeterClient;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(RunArgs args, CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((MinecraftClient)(Object)this);
	}
	
	@Inject(
			method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;",
			at = @At(
					value = "HEAD"
			)
	)
	private void reloadResources(boolean force, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		multimeterClient.reloadResources();
	}
	
	@Inject(
			method = "onResolutionChanged",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/client/util/Window;setScaleFactor(D)V"
			)
	)
	private void onResolutionChanged(CallbackInfo ci) {
		if (multimeterClient != null) {
			multimeterClient.getHUD().resetSize();
		}
	}
	
	@Inject(
			method = "tick()V",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/client/tutorial/TutorialManager;tick()V"
			)
	)
	private void tickTutorial(CallbackInfo ci) {
		multimeterClient.getTutorial().tick();
	}
	
	@Inject(
			method = "handleInputEvents",
			at = @At(
					value = "HEAD"
			)
	)
	private void handleInputEvents(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleKeyBindings();
	}
	
	@Inject(
			method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onDisconnect(Screen screen, CallbackInfo ci) {
		multimeterClient.onDisconnect();
	}
	
	@Inject(
			method = "close",
			at = @At(
					value = "HEAD"
			)
	)
	private void onClose(CallbackInfo ci) {
		multimeterClient.onShutdown();
	}
	
	@Override
	public MultimeterClient getMultimeterClient() {
		return multimeterClient;
	}
}
