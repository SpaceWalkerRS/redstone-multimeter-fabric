package rsmm.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.interfaces.mixin.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {
	
	private MultimeterClient multimeterClient;
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(RunArgs args, CallbackInfo ci) {
		this.multimeterClient = new MultimeterClient((MinecraftClient)(Object)this);
		
		this.multimeterClient.onStartup();
	}
	
	@Inject(
			method = "handleInputEvents",
			at = @At(
					value = "HEAD"
			)
	)
	private void onHandleInputEventsInjectAtHead(CallbackInfo ci) {
		multimeterClient.getInputHandler().handleInputEvents();
	}
	
	@Override
	public MultimeterClient getMultimeterClient() {
		return multimeterClient;
	}
}
