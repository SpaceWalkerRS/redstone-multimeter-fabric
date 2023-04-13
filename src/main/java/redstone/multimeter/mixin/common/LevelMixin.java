package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.Level;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(Level.class)
public class LevelMixin implements TickTaskExecutor {

	@Shadow public boolean isClientSide() { return false; }

	@Inject(
		method = "tickBlockEntities",
		at = @At(
			value = "HEAD"
		)
	)
	private void startTickTaskBlockEntities(CallbackInfo ci) {
		rsmm$startTickTask(TickTask.BLOCK_ENTITIES);
	}

	@Inject(
		method = "tickBlockEntities",
		at = @At(
			value = "TAIL"
		)
	)
	private void endTickTaskBlockEntities(CallbackInfo ci) {
		rsmm$endTickTask();
	}
}
