package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;

public interface IWorld {
	
	default void startTickTaskRSMM(TickTask task) {
		
	}
	
	default void endTickTaskRSMM() {
		
	}
	
	default void swapTickTaskRSMM(TickTask task) {
		
	}
}
