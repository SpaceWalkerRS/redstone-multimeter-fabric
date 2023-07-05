package redstone.multimeter.mixin.common;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.chunk.ServerChunkCache;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

	@Shadow @Final private ServerWorld world;

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;)Z",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskUnloadChunks(BooleanSupplier hasTimeLeft, CallbackInfoReturnable<Boolean> ci) {
		((TickTaskExecutor)world).rsmm$startTickTask(TickTask.UNLOAD_CHUNKS);
	}

	@Inject(
		method = "tick(Ljava/util/function/BooleanSupplier;)Z",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskUnloadChunks(BooleanSupplier hasTimeLeft, CallbackInfoReturnable<Boolean> ci) {
		((TickTaskExecutor)world).rsmm$endTickTask();
	}
}
