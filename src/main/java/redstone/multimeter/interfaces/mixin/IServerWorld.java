package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.common.TickPhase;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

public interface IServerWorld extends IWorld {
	
	@Override
	default void onTickPhase(TickPhase tickPhase) {
		getMultimeterServer().getMultimeter().onTickPhase(tickPhase);
	}
	
	public MultimeterServer getMultimeterServer();
	
	default Multimeter getMultimeter() {
		return getMultimeterServer().getMultimeter();
	}
}
