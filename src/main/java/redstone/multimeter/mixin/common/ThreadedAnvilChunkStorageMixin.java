package redstone.multimeter.mixin.common;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
	
	@Shadow @Final private ServerWorld world;
	
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskUnloadChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		((TickTaskExecutor)world).startTickTaskRSMM(TickTask.UNLOAD_CHUNKS);
	}
	
	@Inject(
			method = "tick(Ljava/util/function/BooleanSupplier;)V",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskUnloadChunks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		((TickTaskExecutor)world).endTickTaskRSMM();
	}
}
