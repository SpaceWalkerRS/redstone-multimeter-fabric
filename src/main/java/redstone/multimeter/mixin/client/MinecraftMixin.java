package redstone.multimeter.mixin.client;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

	private MultimeterClient multimeterClient;

	@Inject(
		method = "<init>",
		at = @At(
			value = "TAIL"
		)
	)
	private void init(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((Minecraft)(Object)this);
		// all initialization happens in the constructor
		this.multimeterClient.onStartup();
	}

	@Inject(
		method = "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;",
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

	@Redirect(
		method = "handleKeybinds",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/client/Options;keyHotbarSlots:[Lnet/minecraft/client/KeyMapping;"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z"
		)
	)
	private boolean handleHotbarKeybinds(KeyMapping keybind, @Local int slot) {
		return keybind.consumeClick() && !multimeterClient.getInputHandler().handleHotbarKeybinds(slot);
	}

	@Inject(
		method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V",
		at = @At(
			value = "HEAD"
		)
	)
	private void disconnect(Screen screen, boolean transferring, CallbackInfo ci) {
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
