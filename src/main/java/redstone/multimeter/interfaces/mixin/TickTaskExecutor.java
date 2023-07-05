package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;

public interface TickTaskExecutor {

	default void rsmm$startTickTask(TickTask task, String... args) {
	}

	default void rsmm$endTickTask() {
	}

	default void rsmm$swapTickTask(TickTask task, String... args) {
	}

	default TickTask getCurrentTickTask() {
		return TickTask.UNKNOWN;
	}
}
