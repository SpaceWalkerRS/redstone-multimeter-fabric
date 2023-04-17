package redstone.multimeter.interfaces.mixin;

import java.util.function.Consumer;

import net.minecraft.world.ticks.ScheduledTick;

public interface ILevelTicks {

	public void rsmm$setScheduleListener(Consumer<ScheduledTick<?>> listener);

	public void rsmm$setTickListener(Consumer<ScheduledTick<?>> listener);

}
