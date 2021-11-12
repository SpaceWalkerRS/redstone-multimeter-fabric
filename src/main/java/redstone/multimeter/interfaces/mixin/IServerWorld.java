package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

public interface IServerWorld extends IWorld {
	
	@Override
	default void startTickTask(TickTask task) {
		getMultimeterServer().startTickTask(task);
	}
	
	@Override
	default void endTickTask() {
		getMultimeterServer().endTickTask();
	}
	
	@Override
	default void swapTickTask(TickTask task) {
		getMultimeterServer().swapTickTask(task);
	}
	
	public MultimeterServer getMultimeterServer();
	
	default Multimeter getMultimeter() {
		return getMultimeterServer().getMultimeter();
	}
}
