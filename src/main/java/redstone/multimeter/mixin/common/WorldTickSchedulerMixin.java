package redstone.multimeter.mixin.common;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.WorldTickScheduler;

import redstone.multimeter.interfaces.mixin.IWorldTickScheduler;

@Mixin(WorldTickScheduler.class)
public class WorldTickSchedulerMixin implements IWorldTickScheduler {
	
	private Consumer<OrderedTick<?>> tickScheduleConsumerRSMM;
	private Consumer<OrderedTick<?>> tickExecutionConsumerRSMM;
	
	@Inject(
			method = "schedule(Lnet/minecraft/world/tick/OrderedTick;)V",
			at = @At(
					value = "HEAD"
			)
	)
	private void logTickScheduled(OrderedTick<?> scheduledTick, CallbackInfo ci) {
		if (tickScheduleConsumerRSMM != null) {
			tickScheduleConsumerRSMM.accept(scheduledTick);
		}
	}
	
	@Inject(
			method = "tick(Ljava/util/function/BiConsumer;)V",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"
			)
	)
	private void logScheduledTick(BiConsumer<BlockPos, ?> ticker, CallbackInfo ci, OrderedTick<?> scheduledTick) {
		if (tickExecutionConsumerRSMM != null) {
			tickExecutionConsumerRSMM.accept(scheduledTick);
		}
	}
	
	@Override
	public void setTickScheduleConsumerRSMM(Consumer<OrderedTick<?>> consumer) {
		this.tickScheduleConsumerRSMM = consumer;
	}

	@Override
	public void setTickExecutionConsumerRSMM(Consumer<OrderedTick<?>> consumer) {
		this.tickExecutionConsumerRSMM = consumer;
	}
}
