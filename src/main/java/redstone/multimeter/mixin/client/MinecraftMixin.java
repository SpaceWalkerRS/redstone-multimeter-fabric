package redstone.multimeter.mixin.client;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.screen.ScreenWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

	@Shadow
	private Screen screen;

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
		method = "reloadResources()V",
		at = @At(
			value = "HEAD"
		)
	)
	private void reloadResources(CallbackInfo ci) {
		if (multimeterClient != null) {
			multimeterClient.reloadResources();
		}
	}

	@Inject(
		method = "onResolutionChanged(II)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;onResolutionChanged()V"
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
			target = "Lnet/minecraft/client/world/ClientWorld;tick()V"
		)
	)
	private void tickTutorial(CallbackInfo ci) {
		multimeterClient.getTutorial().tick();
	}

	@Inject(
		method = "tick()V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=mouse"
		)
	)
	private void handleKeybinds(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleKeybinds();
	}

	@Redirect(
		method = "tick()V",
		at = @At(
			value = "INVOKE",
			remap = false,
			target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"
		)
	)
	private int handleScroll() {
		int scrollY = Mouse.getEventDWheel();

		if (multimeterClient.getInputHandler().handleMouseScroll(0, scrollY)) {
			return 0;
		}

		return scrollY;
	}

	@Inject(
		method = "setWorld(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V",
		at = @At(
			value = "HEAD"
		)
	)
	private void setWorld(ClientWorld world, String message, CallbackInfo ci) {
		if (world == null) {
			multimeterClient.onDisconnect();
		}
	}

	@Inject(
		method = "openScreen",
		at = @At(
			value = "TAIL"
		)
	)
	private void openScreen(Screen screen, CallbackInfo ci) {
		if (this.screen != null && !(this.screen instanceof ScreenWrapper)) {
			multimeterClient.getTutorial().onScreenOpened(this.screen);
		}
	}

	@Inject(
		method = "stop",
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
