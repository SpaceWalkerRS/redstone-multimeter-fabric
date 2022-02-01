package redstone.multimeter.mixin.common;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.util.thread.ReentrantThreadExecutor;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements IMinecraftServer {
	
	private MultimeterServer multimeterServer;
	
	public MinecraftServerMixin(String name) {
		super(name);
	}
	
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
		startTickTaskRSMM(TickTask.PACKETS);
	}
	
	@Inject(
			method = "runTasksTillTickEnd",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPackets(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskTick(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.TICK);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lorg/apache/logging/log4j/Logger;debug(Ljava/lang/String;)V",
					args = "ldc=Autosave started"
			)
	)
	private void startTickTaskAutosave(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.AUTOSAVE);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lorg/apache/logging/log4j/Logger;debug(Ljava/lang/String;)V",
					args = "ldc=Autosave finished"
			)
	)
	private void endTickTaskAutosave(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskTick(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=commandFunctions"
			)
	)
	private void startTickTaskCommandFunctions(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.COMMAND_FUNCTIONS);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=levels"
			)
	)
	private void swapTickTaskLevels(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.LEVELS);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=connection"
			)
	)
	private void swapTickTaskConnections(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.CONNECTIONS);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=players"
					)
			)
	private void swapTickTaskPlayerPing(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.PLAYER_PING);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=server gui refresh"
			)
	)
	private void swapTickTaskServerGui(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.SERVER_GUI);
	}
	
	@Inject(
			method = "tickWorlds",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskServerGui(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "reloadResources",
			at = @At(
					value = "HEAD"
			)
	)
	private void onReloadResources(Collection<String> datapacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		execute(() -> multimeterServer.getMultimeter().reloadOptions());
	}
	
	@Inject(
			method = "startTickMetrics",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart(CallbackInfo ci) {
		multimeterServer.tickStart();
	}
	
	@Inject(
			method = "endTickMetrics",
			at = @At(
					value = "RETURN"
			)
	)
	private void onTickEnd(CallbackInfo ci) {
		multimeterServer.tickEnd();
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return multimeterServer;
	}
	
	@Override
	public boolean isPausedRSMM() {
		return false;
	}
}
