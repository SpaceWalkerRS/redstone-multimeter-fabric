package redstone.multimeter.mixin.common;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;

import redstone.multimeter.interfaces.mixin.ILevelTicks;

@Mixin(LevelTicks.class)
public class LevelTicksMixin implements ILevelTicks {

	private Consumer<ScheduledTick<?>> rsmm$scheduleListener;
	private Consumer<ScheduledTick<?>> rsmm$tickListener;

	@Inject(
		method = "schedule(Lnet/minecraft/world/ticks/ScheduledTick;)V",
		at = @At(
			value = "HEAD"
		)
	)
	private void logSchedule(ScheduledTick<?> scheduledTick, CallbackInfo ci) {
		if (rsmm$scheduleListener != null) {
			rsmm$scheduleListener.accept(scheduledTick);
		}
	}

	@Inject(
		method = "runCollectedTicks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			shift = Shift.BEFORE,
			target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"
		)
	)
	private void logTick(BiConsumer<BlockPos, ?> ticker, CallbackInfo ci, ScheduledTick<?> scheduledTick) {
		if (rsmm$tickListener != null) {
			rsmm$tickListener.accept(scheduledTick);
		}
	}

	@Override
	public void rsmm$setScheduleListener(Consumer<ScheduledTick<?>> listener) {
		this.rsmm$scheduleListener = listener;
	}

	@Override
	public void rsmm$setTickListener(Consumer<ScheduledTick<?>> listener) {
		this.rsmm$tickListener = listener;
	}
}
