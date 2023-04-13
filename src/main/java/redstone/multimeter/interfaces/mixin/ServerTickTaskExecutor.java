package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.server.MultimeterServer;

public interface ServerTickTaskExecutor extends TickTaskExecutor {

	@Override
	default void rsmm$startTickTask(boolean updateTree, TickTask task, String... args) {
		MultimeterServer server = getMultimeterServer();

		if (server != null) {
			server.startTickTask(updateTree, task, args);
		}
	}

	@Override
	default void rsmm$endTickTask(boolean updateTree) {
		MultimeterServer server = getMultimeterServer();

		if (server != null) {
			server.endTickTask(updateTree);
		}
	}

	@Override
	default void rsmm$swapTickTask(boolean updateTree, TickTask task, String... args) {
		MultimeterServer server = getMultimeterServer();

		if (server != null) {
			server.swapTickTask(updateTree, task, args);
		}
	}

	public MultimeterServer getMultimeterServer();

}
