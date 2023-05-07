package redstone.multimeter.interfaces.mixin;

import net.minecraft.world.ticks.ScheduledTick;

public interface ScheduledTickListener {

	void rsmm$scheduleTick(ScheduledTick<?> scheduledTick);

	void rsmm$runTick(ScheduledTick<?> scheduledTick);

}
