package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

public interface IServerWorld extends IWorld {
	
	@Override
	default void startTickTaskRSMM(TickTask task) {
		getMultimeterServer().startTickTask(task);
	}
	
	@Override
	default void endTickTaskRSMM() {
		getMultimeterServer().endTickTask();
	}
	
	@Override
	default void swapTickTaskRSMM(TickTask task) {
		getMultimeterServer().swapTickTask(task);
	}
	
	public MultimeterServer getMultimeterServer();
	
	default Multimeter getMultimeter() {
		return getMultimeterServer().getMultimeter();
	}
}
