package redstone.multimeter.mixin.server;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TickDurationMonitor;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
	
	private MultimeterServer multimeterServer;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(CallbackInfo ci) {
		this.multimeterServer = new MultimeterServer((MinecraftServer)(Object)this);
	}
	
	@Inject(
			method = "method_16208",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskPackets(CallbackInfo ci) {
		multimeterServer.startTickTask(TickTask.PACKETS);
	}
	
	@Inject(
			method = "method_16208",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPackets(CallbackInfo ci) {
		multimeterServer.endTickTask();
	}
	
	@Inject(
			method = "reloadResources",
			at = @At(
					value = "HEAD"
			)
	)
	private void onReloadResources(Collection<String> datapacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		multimeterServer.getMultimeter().reloadOptions();
	}
	
	@Inject(
			method = "startMonitor",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart(TickDurationMonitor minitor, CallbackInfo ci) {
		multimeterServer.tickStart();
	}
	
	@Inject(
			method = "endMonitor",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickEnd(TickDurationMonitor monitor, CallbackInfo ci) {
		multimeterServer.tickEnd();
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
