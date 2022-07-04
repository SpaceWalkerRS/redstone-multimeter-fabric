package redstone.multimeter.interfaces.mixin;

import java.util.function.Consumer;

import net.minecraft.world.tick.OrderedTick;

public interface IWorldTickScheduler {
	
	public void setTickScheduleConsumerRSMM(Consumer<OrderedTick<?>> consumer);
	
	public void setTickExecutionConsumerRSMM(Consumer<OrderedTick<?>> consumer);
	
}
