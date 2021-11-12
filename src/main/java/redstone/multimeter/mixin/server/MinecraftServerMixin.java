package redstone.multimeter.mixin.server;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;

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
			method = "runTasksTillTickEnd",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskPackets(CallbackInfo ci) {
		multimeterServer.startTickTask(TickTask.PACKETS);
	}
	
	@Inject(
			method = "runTasksTillTickEnd",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPackets(CallbackInfo ci) {
		multimeterServer.endTickTask();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"
			)
	)
	private void onTickStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		multimeterServer.tickStart();
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
			method = "startTickMetrics",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart() {
		multimeterServer.tickStart();
	}
	
	@Inject(
			method = "endTickMetrics",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickEnd() {
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
