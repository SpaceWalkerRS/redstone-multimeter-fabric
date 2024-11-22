package redstone.multimeter.mixin.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Connection;
import net.minecraft.network.LocalConnection;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IConnection;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {

	@Shadow
	private Screen screen;
	@Shadow
	private boolean paused;

	private MultimeterClient multimeterClient;

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

	@Redirect(
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
	private int handleHotbarKeybinds(@Local(ordinal = 0) int slot) {
		return Keyboard.getEventKey() == 2 + slot && multimeterClient.getInputHandler().handleHotbarKeybinds(slot) ? -1 : Keyboard.getEventKey();
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
