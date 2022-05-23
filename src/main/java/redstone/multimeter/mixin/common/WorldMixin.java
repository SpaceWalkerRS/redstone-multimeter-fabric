package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.World;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.TickTaskExecutor;

@Mixin(World.class)
public abstract class WorldMixin implements TickTaskExecutor {
	
	@Shadow public abstract boolean isClient();
	
	@Inject(
			method = "tickBlockEntities",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskBlockEntities(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.BLOCK_ENTITIES);
	}
	
	@Inject(
			method = "tickBlockEntities",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskBlockEntities(CallbackInfo ci) {
		endTickTaskRSMM();
	}
}
