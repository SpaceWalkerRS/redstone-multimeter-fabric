package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.server.MultimeterServer;

public interface MultimeterServerProvider extends TickTaskExecutor {
	
	@Override
	default void startTickTaskRSMM(boolean updateTree, TickTask task, String... args) {
		getMultimeterServer().startTickTask(updateTree, task, args);
	}
	
	@Override
	default void endTickTaskRSMM(boolean updateTree) {
		getMultimeterServer().endTickTask(updateTree);
	}
	
	@Override
	default void swapTickTaskRSMM(boolean updateTree, TickTask task, String... args) {
		getMultimeterServer().swapTickTask(updateTree, task, args);
	}
	
	public MultimeterServer getMultimeterServer();
	
}
