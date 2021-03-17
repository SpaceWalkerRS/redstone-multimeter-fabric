package rsmm.fabric.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

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
			method = "runServer",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/util/profiler/Profiler;endTick()V"
			)
	)
	private void onRunServerInjectBeforeEndTick(CallbackInfo ci) {
		multimeterServer.updateMultimeterClients();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickInjectAtHead(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		multimeterServer.tick(shouldKeepTicking);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return multimeterServer;
	}
}
