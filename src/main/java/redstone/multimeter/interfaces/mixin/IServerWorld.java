package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.server.Multimeter;

public interface IServerWorld extends MultimeterServerProvider {
	
	default Multimeter getMultimeter() {
		return getMultimeterServer().getMultimeter();
	}
}
