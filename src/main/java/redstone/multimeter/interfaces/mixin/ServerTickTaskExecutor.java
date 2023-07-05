package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.server.MultimeterServer;

public interface ServerTickTaskExecutor extends TickTaskExecutor {

	@Override
	default void rsmm$startTickTask(TickTask task, String... args) {
		getMultimeterServer().startTickTask(task, args);
	}

	@Override
	default void rsmm$endTickTask() {
		getMultimeterServer().endTickTask();
	}

	@Override
	default void rsmm$swapTickTask(TickTask task, String... args) {
		getMultimeterServer().swapTickTask(task, args);
	}

	@Override
	default TickTask getCurrentTickTask() {
		return getMultimeterServer().getCurrentTickTask();
	}

	public MultimeterServer getMultimeterServer();

}
