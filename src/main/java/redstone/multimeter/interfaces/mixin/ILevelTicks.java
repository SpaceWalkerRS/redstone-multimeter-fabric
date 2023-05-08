package redstone.multimeter.interfaces.mixin;

public interface ILevelTicks {

	void rsmm$setListener(ScheduledTickListener listener);

	ScheduledTickListener rsmm$getListener();

}
