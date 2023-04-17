package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;

public interface TickTaskExecutor {

	default void rsmm$startTickTask(TickTask task, String... args) {
		rsmm$startTickTask(true, task, args);
	}

	default void rsmm$startTickTask(boolean updateTree, TickTask task, String... args) {
	}

	default void rsmm$endTickTask() {
		rsmm$endTickTask(true);
	}

	default void rsmm$endTickTask(boolean updateTree) {
	}

	default void rsmm$swapTickTask(TickTask task, String... args) {
		rsmm$swapTickTask(true, task, args);
	}

	default void rsmm$swapTickTask(boolean updateTree, TickTask task, String... args) {
	}
}
