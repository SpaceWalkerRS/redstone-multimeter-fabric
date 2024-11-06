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
		method = "tick(Ljava/util/function/BooleanSupplier;Z)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
			args = "ldc=purge"
		)
	)
	private void startTickTaskPurgeUnloadedChunks(BooleanSupplier hasTimeLeft, boolean tickChunks, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$startTickTask(TickTask.PURGE_UNLOADED_CHUNKS);
	}

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;Z)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=chunks"
		)
	)
	private void swapTickTaskTickChunks(BooleanSupplier hasTimeLeft, boolean tickChunks, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$swapTickTask(TickTask.TICK_CHUNKS);
	}

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;Z)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=unload"
		)
	)
	private void swapTickTaskUnloadChunks(BooleanSupplier hasTimeLeft, boolean tickChunks, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$swapTickTask(TickTask.UNLOAD_CHUNKS);
	}

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;Z)V",
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
	private void endTickTaskUnloadChunks(BooleanSupplier hasTimeLeft, boolean tickChunks, CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$endTickTask();
	}

	@Inject(
		method = "broadcastChangedChunks",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskBroadcastChunks(CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$startTickTask(TickTask.BROADCAST_CHUNKS);
	}

	@Inject(
		method = "broadcastChangedChunks",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskBroadcastChunks(CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$endTickTask();
	}

	@Inject(
		method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;JLjava/util/List;)V",
		at = @At(
			value = "INVOKE_STRING",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
			args = "ldc=customSpawners"
		)
	)
	private void startTickTaskCustomMobSpawning(CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$startTickTask(TickTask.CUSTOM_MOB_SPAWNING);
	}

	@Inject(
		method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;JLjava/util/List;)V",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskCustomMobSpawning(CallbackInfo ci) {
		((TickTaskExecutor)level).rsmm$endTickTask();
	}
}
