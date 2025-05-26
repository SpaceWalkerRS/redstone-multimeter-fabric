package redstone.multimeter.mixin.client;

import org.lwjgl.input.Mouse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.world.ClientWorld;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.screen.ScreenWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

	@Shadow
	private GameOptions options;
	@Shadow
	private Screen screen;
	@Shadow
	private long sysTime;

	private MultimeterClient multimeterClient;
	private long savedSysTime;

	@Shadow
	private static long getTime() { return 0L; }

	@Inject(
		method = "init",
		at = @At(
			value = "TAIL"
		)
	)
	private void init(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((Minecraft)(Object)this);
		this.multimeterClient.onStartup();
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

	@Inject(
		method = "tick()V",
		slice = @Slice(
			from = @At(
				value = "INVOKE_STRING",
				target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
				args = "ldc=mouse"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/client/Minecraft;getTime()J"
		)
	)
	private void handleScroll(CallbackInfo ci) {
		savedSysTime = sysTime;

		if (getTime() - sysTime <= 200) {
			int scrollY = Mouse.getDWheel();

			if (multimeterClient.getInputHandler().handleMouseScroll(0, scrollY)) {
				// prevent vanilla handling of scroll event
				sysTime = -Integer.MIN_VALUE;
			}
		}
	}

	@Inject(
		method = "tick()V",
		slice = @Slice(
			from = @At(
				value = "INVOKE_STRING",
				target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
				args = "ldc=mouse"
			)
		),
		at = @At(
			value = "FIELD",
			ordinal = 0,
			shift = Shift.AFTER,
			target = "Lnet/minecraft/client/Minecraft;sysTime:J"
		)
	)
	private void restoreScroll(CallbackInfo ci) {
		sysTime = savedSysTime;
	}

	@Inject(
		method = "tick()V",
		slice = @Slice(
			from = @At(
				value = "CONSTANT",
				ordinal = 1,
				args = "intValue=9"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/client/options/KeyBinding;consumeClick()Z"
		)
	)
	private void handleHotbarKeybinds(CallbackInfo ci, @Local int slot) {
		KeyBinding keybind = options.horbarKeyBindings[slot];
		int key = keybind.getKeyCode();

		while (keybind.consumeClick()) {
			if (!multimeterClient.getInputHandler().handleHotbarKeybinds(slot)) {
				// un-consume the click so the toolbar/
				// select hotbar slot code can be handled
				KeyBinding.click(key);

				break;
			}
		}
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
		if (this.screen != null && !(this.screen instanceof ScreenWrapper) && multimeterClient != null) {
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
