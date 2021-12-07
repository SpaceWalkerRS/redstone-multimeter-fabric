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
	
	private Consumer<OrderedTick<?>> tickConsumer;
	
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
		if (tickConsumer != null) {
			tickConsumer.accept(scheduledTick);
		}
	}
	
	@Override
	public void setTickConsumerRSMM(Consumer<OrderedTick<?>> consumer) {
		this.tickConsumer = consumer;
	}
}
