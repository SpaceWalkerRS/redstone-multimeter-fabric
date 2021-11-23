package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {
	
	private MultimeterClient multimeterClient;
	
	@Inject(
			method = "init",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((MinecraftClient)(Object)this);
	}
	
	@Inject(
			method = "method_1521",
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
			method = "handleInputEvents",
			at = @At(
					value = "HEAD"
			)
	)
	private void handleInputEvents(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleKeyBindings();
	}
	
	@Inject(
			method = "method_1550",
			at = @At(
					value = "HEAD"
			)
	)
	private void onDisconnect(ClientWorld world, Screen screen, CallbackInfo ci) {
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
