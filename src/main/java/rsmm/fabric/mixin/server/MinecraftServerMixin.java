package rsmm.fabric.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

import rsmm.fabric.common.TickPhase;
import rsmm.fabric.interfaces.mixin.IMinecraftServer;
import rsmm.fabric.server.MultimeterServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
	
	private MultimeterServer multimeterServer;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInitInjectAtReturn(CallbackInfo ci) {
		this.multimeterServer = new MultimeterServer((MinecraftServer)(Object)this);
	}
	
	@Inject(
			method = "method_16208",
			at = @At(
					value = "HEAD"
			)
	)
	private void onMethod_16208InjectAtHead(CallbackInfo ci) {
		multimeterServer.getMultimeter().onTickPhase(TickPhase.HANDLE_PACKETS);
	}
	
	@Inject(
			method = "method_16208",
			at = @At(
					value = "RETURN"
			)
	)
	private void onMethod_16208InjectAtReturn(CallbackInfo ci) {
		multimeterServer.tickEnd();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"
			)
	)
	private void onTickInjectAtHead(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		multimeterServer.tickStart();
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return multimeterServer;
	}
	
	@Override
	public boolean isPaused() {
		return false;
	}
}
