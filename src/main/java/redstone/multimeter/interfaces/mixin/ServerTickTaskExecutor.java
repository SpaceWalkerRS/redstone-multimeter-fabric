package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.server.MultimeterServer;

public interface ServerTickTaskExecutor extends TickTaskExecutor {

	@Override
	default void rsmm$startTickTask(boolean updateTree, TickTask task, String... args) {
		getMultimeterServer().startTickTask(updateTree, task, args);
	}

	@Override
	default void rsmm$endTickTask(boolean updateTree) {
		getMultimeterServer().endTickTask(updateTree);
	}

	@Override
	default void rsmm$swapTickTask(boolean updateTree, TickTask task, String... args) {
		getMultimeterServer().swapTickTask(updateTree, task, args);
	}

	public MultimeterServer getMultimeterServer();

}
