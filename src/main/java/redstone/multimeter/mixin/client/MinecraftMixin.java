package redstone.multimeter.mixin.client;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraft {
	
	private MultimeterClient multimeterClient;
	
	@Inject(
			method = "init",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((Minecraft)(Object)this);
	}
	
	@Inject(
			method = "refreshResources",
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
			method = "processKeyBinds",
			at = @At(
					value = "HEAD"
			)
	)
	private void handleKeybindings(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleKeyBindings();
	}
	
	@Redirect(
			method = "runTickMouse",
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
			method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void onDisconnect(WorldClient world, String loadingMessage, CallbackInfo ci) {
		if (world == null) {
			multimeterClient.onDisconnect();
		}
	}
	
	@Inject(
			method = "shutdownMinecraftApplet",
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
