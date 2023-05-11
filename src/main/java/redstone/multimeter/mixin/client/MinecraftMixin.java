package redstone.multimeter.mixin.client;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

	private MultimeterClient multimeterClient;

	@Inject(
		method = "init",
		at = @At(
			value = "TAIL"
		)
	)
	private void init(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((Minecraft)(Object)this);
	}

	@Inject(
		method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;",
		at = @At(
			value = "HEAD"
		)
	)
	private void reloadResources(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		multimeterClient.reloadResources();
	}

	@Inject(
		method = "resizeDisplay",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lcom/mojang/blaze3d/platform/Window;setGuiScale(D)V"
		)
	)
	private void resizeDisplay(CallbackInfo ci) {
		if (multimeterClient != null) {
			multimeterClient.getHud().resetSize();
		}
	}

	@Inject(
		method = "tick()V",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/tutorial/Tutorial;tick()V"
		)
	)
	private void tickTutorial(CallbackInfo ci) {
		multimeterClient.getTutorial().tick();
	}

	@Inject(
		method = "handleKeybinds",
		at = @At(
			value = "HEAD"
		)
	)
	private void handleKeybinds(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleKeybinds();
	}

	@Inject(
		method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V",
		at = @At(
			value = "HEAD"
		)
	)
	private void clearLevel(Screen screen, CallbackInfo ci) {
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
