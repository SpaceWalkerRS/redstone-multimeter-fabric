package redstone.multimeter.mixin.common;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

	@Shadow @Final private ServerLevel level;

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
			args = "ldc=purge"
		)
	)
	private void startTickTaskPurgeUnloadedChunks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$startTickTask(TickTask.PURGE_UNLOADED_CHUNKS);
	}

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=chunks"
		)
	)
	private void swapTickTaskTickChunks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$swapTickTask(TickTask.TICK_CHUNKS);
	}

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=unload"
		)
	)
	private void swapTickTaskUnloadChunks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$swapTickTask(TickTask.UNLOAD_CHUNKS);
	}

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;)V",
		slice = @Slice(
			from = @At(
				value = "INVOKE_STRING",
				target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
				args = "ldc=unload"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
		)
	)
	private void endTickTaskUnloadChunks(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$endTickTask();
	}

	@Inject(
		method = "tickChunks",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
			args = "ldc=customSpawners"
		)
	)
	private void startTickTaskCustomMobSpawning(CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$startTickTask(TickTask.CUSTOM_MOB_SPAWNING);
	}

	@Inject(
		method = "tickChunks",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=broadcast"
		)
	)
	private void swapTickTaskBroadcastChunks(CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$swapTickTask(TickTask.BROADCAST_CHUNKS);
	}

	@Inject(
		method = "tickChunks",
		slice = @Slice(
			from = @At(
				value = "INVOKE_STRING",
				target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
				args = "ldc=broadcast"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
		)
	)
	private void endTickTaskBroadcastChunks(CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$endTickTask();
	}
}
