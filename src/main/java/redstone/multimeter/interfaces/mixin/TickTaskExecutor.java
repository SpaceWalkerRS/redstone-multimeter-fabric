package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;

public interface TickTaskExecutor {
	
	default void startTickTaskRSMM(TickTask task, String... args) {
		startTickTaskRSMM(true, task, args);
	}
	
	default void startTickTaskRSMM(boolean updateTree, TickTask task, String... args) {
		
	}
	
	default void endTickTaskRSMM() {
		endTickTaskRSMM(true);
	}
	
	default void endTickTaskRSMM(boolean updateTree) {
		
	}
	
	default void swapTickTaskRSMM(TickTask task, String... args) {
		swapTickTaskRSMM(true, task, args);
	}
	
	default void swapTickTaskRSMM(boolean updateTree, TickTask task, String... args) {
		
	}
}
