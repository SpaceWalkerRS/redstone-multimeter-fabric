package redstone.multimeter.mixin.common;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
	
	@Shadow @Final private ServerWorld world;
	
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=purge"
			)
	)
	private void startpTickTaskPurgeUnloadedChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		((TickTaskExecutor)world).startTickTaskRSMM(TickTask.PURGE_UNLOADED_CHUNKS);
	}
	
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunks"
			)
	)
	private void swapTickTaskTickChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		((TickTaskExecutor)world).swapTickTaskRSMM(TickTask.TICK_CHUNKS);
	}
	
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=unload"
			)
	)
	private void swapTickTaskUnloadChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		((TickTaskExecutor)world).swapTickTaskRSMM(TickTask.UNLOAD_CHUNKS);
	}
	
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			slice = @Slice(
					from = @At(
							value = "INVOKE_STRING",
							target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
							args = "ldc=unload"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
			)
	)
	private void endTickTaskUnloadChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		((TickTaskExecutor)world).endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickChunks",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;spawnEntities(Lnet/minecraft/server/world/ServerWorld;ZZ)V"
			)
	)
	private void startTickTaskCustomMobSpawning(CallbackInfo ci) {
		((IServerWorld)world).startTickTaskRSMM(TickTask.CUSTOM_MOB_SPAWNING);
	}
	
	@Inject(
			method = "tickChunks",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;spawnEntities(Lnet/minecraft/server/world/ServerWorld;ZZ)V"
			)
	)
	private void endTickTaskCustomMobSpawning(CallbackInfo ci) {
		((IServerWorld)world).endTickTaskRSMM();
	}
}
