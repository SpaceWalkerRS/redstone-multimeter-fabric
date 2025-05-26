package redstone.multimeter.mixin.client;

import org.lwjgl.input.Keyboard;
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
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Connection;
import net.minecraft.network.LocalConnection;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IConnection;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

	@Shadow
	private GameOptions options;
	@Shadow
	private Screen screen;
	@Shadow
	private LocalClientPlayerEntity player;
	@Shadow
	private boolean paused;
	@Shadow
	private long sysTime;

	private MultimeterClient multimeterClient;
	private long savedSysTime;
	private int savedHotbarSlot;

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
		method = "onResolutionChanged(II)V",
		at = @At(
			value = "TAIL"
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
			value = "HEAD"
		)
	)
	private void tickConnection(CallbackInfo ci) {
		ClientNetworkHandler networkHandler = ((Minecraft) (Object) this).getNetworkHandler();
		Connection connection = networkHandler != null ? networkHandler.getConnection() : null;

		if (connection != null && connection instanceof LocalConnection && paused) {
			((IConnection) connection).rsmm$handleRsmmPackets();;
		}
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
			int scrollY = Mouse.getEventDWheel();

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
				ordinal = 0,
				args = "intValue=9"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lorg/lwjgl/input/Keyboard;getEventKey()I"
		)
	)
	private void handleHotbarKeybinds(CallbackInfo ci, @Local(ordinal = 0) int slot) {
		savedHotbarSlot = -1;

		if (Keyboard.getEventKey() == 2 + slot) {
			if (multimeterClient.getInputHandler().handleHotbarKeybinds(slot)) {
				// save the selected slot before vanilla handling of
				// the hotbar slot keys - then reset afterwards
				// after all, rsmm's handling was successful!
				savedHotbarSlot = player.inventory.selectedSlot;
			}
		}
	}

	@Inject(
		method = "tick()V",
		slice = @Slice(
			from = @At(
				value = "CONSTANT",
				ordinal = 0,
				args = "intValue=9"
			)
		),
		at = @At(
			value = "FIELD",
			ordinal = 0,
			target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z"
		)
	)
	private void restoreHotbarKeybinds(CallbackInfo ci) {
		if (savedHotbarSlot >= 0) {
			player.inventory.selectedSlot = savedHotbarSlot;
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
