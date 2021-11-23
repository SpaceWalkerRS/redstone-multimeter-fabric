package redstone.multimeter.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		multimeterServer.tickStart();
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15396(Ljava/lang/String;)V",
					args = "ldc=jobs"
			)
	)
	private void startTickTaskPackets(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		multimeterServer.startTickTask(TickTask.PACKETS);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=commandFunctions"
			)
	)
	private void swapTickTaskCommandFunctions(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		multimeterServer.swapTickTask(TickTask.COMMAND_FUNCTIONS);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/ProfilerSystem;method_15405(Ljava/lang/String;)V",
					args = "ldc=levels"
			)
	)
	private void endTickTaskCommandFunctions(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		multimeterServer.endTickTask();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPacketsAndEndTick(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		multimeterServer.endTickTask();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickEnd(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		multimeterServer.tickEnd();
	}
	
	@Inject(
			method = "reload",
			at = @At(
					value = "HEAD"
			)
	)
	private void onReload(CallbackInfo ci) {
		multimeterServer.getMultimeter().reloadOptions();
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
