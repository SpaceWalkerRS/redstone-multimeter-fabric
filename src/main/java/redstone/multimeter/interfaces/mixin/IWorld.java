package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;

public interface IWorld {
	
	default void startTickTask(TickTask task) {
		
	}
	
	default void endTickTask() {
		
	}
	
	default void swapTickTask(TickTask task) {
		
	}
}
