package redstone.multimeter.mixin.common;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;

import redstone.multimeter.interfaces.mixin.ILevelTicks;
import redstone.multimeter.interfaces.mixin.ScheduledTickListener;

@Mixin(LevelTicks.class)
public class LevelTicksMixin implements ILevelTicks {

	private ScheduledTickListener rsmm$listener;

	@Inject(
		method = "schedule(Lnet/minecraft/world/ticks/ScheduledTick;)V",
		at = @At(
			value = "HEAD"
		)
	)
	private void logSchedule(ScheduledTick<?> scheduledTick, CallbackInfo ci) {
		if (rsmm$listener != null) {
			rsmm$listener.rsmm$scheduleTick(scheduledTick);
		}
	}

	@Inject(
		method = "runCollectedTicks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"
		)
	)
	private void logTick(BiConsumer<BlockPos, ?> ticker, CallbackInfo ci, ScheduledTick<?> scheduledTick) {
		if (rsmm$listener != null) {
			rsmm$listener.rsmm$runTick(scheduledTick);
		}
	}

	@Override
	public void rsmm$setListener(ScheduledTickListener listener) {
		this.rsmm$listener = listener;
	}

	@Override
	public ScheduledTickListener rsmm$getListener() {
		return rsmm$listener;
	}
}
