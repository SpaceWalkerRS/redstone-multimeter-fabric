package redstone.multimeter.interfaces.mixin;

import java.util.function.Consumer;

import net.minecraft.world.tick.OrderedTick;

public interface IWorldTickScheduler {
	
	public void setTickConsumerRSMM(Consumer<OrderedTick<?>> consumer);
	
}
