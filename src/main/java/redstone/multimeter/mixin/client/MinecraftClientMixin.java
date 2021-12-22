package redstone.multimeter.mixin.client;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {
	
	@Shadow private Screen currentScreen;
	
	private MultimeterClient multimeterClient;
	
	@Inject(
			method = "initializeGame",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((MinecraftClient)(Object)this);
	}
	
	@Inject(
			method = "stitchTextures",
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
			method = "tick",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							ordinal = 0,
							target = "Lnet/minecraft/client/gui/screen/Screen;passEvents:Z"
					)
			),
			at = @At(
					value = "FIELD",
					ordinal = 0,
					target = "Lnet/minecraft/client/MinecraftClient;world:Lnet/minecraft/client/world/ClientWorld;"
			)
	)
	private void handleInputEvents(CallbackInfo ci) {
		if (currentScreen == null || currentScreen.passEvents) {
			multimeterClient.getInputHandler().handleKeyBindings();
		}
	}
	
	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"
			)
	)
	private int onGetEventDWheel() {
		int scrollY = Mouse.getEventDWheel();
		
		if (multimeterClient.getInputHandler().handleMouseScroll(0, scrollY)) {
			return 0;
		}
		
		return scrollY;
	}
	
	@Inject(
			method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onDisconnect(ClientWorld world, String name, CallbackInfo ci) {
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
	private void onStop(CallbackInfo ci) {
		multimeterClient.onShutdown();
	}
	
	@Override
	public MultimeterClient getMultimeterClient() {
		return multimeterClient;
	}
}
