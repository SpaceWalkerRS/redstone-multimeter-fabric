package redstone.multimeter.mixin.common.compat.subtick;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;

import redstone.multimeter.interfaces.mixin.ILevelTicks;
import redstone.multimeter.interfaces.mixin.ScheduledTickListener;

@Pseudo
@Mixin(targets = "subtick.queues.ScheduledTickQueue")
public class ScheduledTickQueueMixin {

	@Shadow @Final private LevelTicks<?> levelTicks;

	@Inject(
		method = "step",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"
		)
	)
	private void logTick(int count, BlockPos pos, int range, CallbackInfoReturnable<Pair<Integer, Boolean>> cir, int executed_steps, ScheduledTick<?> scheduledTick) {
		ScheduledTickListener listener = ((ILevelTicks)levelTicks).rsmm$getListener();

		if (listener != null) {
			listener.rsmm$runTick(scheduledTick);
		}
	}
}
