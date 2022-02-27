package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
	
	@Shadow @Final private ServerWorld world;
	
	@Inject(
			method = "tickChunks",
			at = @At(
					value = "HEAD"
			)
	)
	private void swapTickTaskTickChunks(CallbackInfo ci) {
		// The 'purge unloaded chunks' task was started from the ServerWorldMixin class...
		// It's some jank workaround for a bug that means we cannot inject into the
		// ServerChunkManager#tick(Ljava/util/function/BooleanSupplier;)V method.
		((TickTaskExecutor)world).swapTickTaskRSMM(TickTask.TICK_CHUNKS);
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
		((TickTaskExecutor)world).startTickTaskRSMM(TickTask.CUSTOM_MOB_SPAWNING);
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
		((TickTaskExecutor)world).endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickChunks",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskTickChunks(CallbackInfo ci) {
		((TickTaskExecutor)world).endTickTaskRSMM();
	}
}
