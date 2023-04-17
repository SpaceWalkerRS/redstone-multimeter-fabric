package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.server.Multimeter;

public interface IServerLevel extends ServerTickTaskExecutor {

	default Multimeter getMultimeter() {
		return getMultimeterServer().getMultimeter();
	}
}
