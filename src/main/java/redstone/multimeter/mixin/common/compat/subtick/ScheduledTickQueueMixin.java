package redstone.multimeter.mixin.common.compat.subtick;

import org.apache.commons.lang3.tuple.Triple;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ticks.ScheduledTick;

import redstone.multimeter.interfaces.mixin.ScheduledTickListener;

import subtick.queues.ScheduledTickQueue;

@Pseudo
@Mixin(ScheduledTickQueue.class)
public class ScheduledTickQueueMixin {

	private ScheduledTickListener rsmm$listener;

	@Inject(
		method = "start",
		remap = false,
		at = @At(
			value = "HEAD"
		)
	)
	private void start(ServerLevel level, CallbackInfo ci) {
		this.rsmm$listener = (ScheduledTickListener)level;
	}

	@Inject(
		method = "step",
		remap = false,
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"
		)
	)
	private void logTick(int count, BlockPos pos, int range, CallbackInfoReturnable<Triple<Integer, Integer, Boolean>> cir, int executed_steps, int success_steps, ScheduledTick<?> scheduledTick) {
		if (rsmm$listener != null) {
			rsmm$listener.rsmm$runTick(scheduledTick);
		}
	}

	@Inject(
		method = "end",
		remap = false,
		at = @At(
			value = "HEAD"
		)
	)
	private void end(CallbackInfo ci) {
		rsmm$listener = null;
	}
}
