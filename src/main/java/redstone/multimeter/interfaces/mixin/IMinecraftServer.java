package redstone.multimeter.interfaces.mixin;

import redstone.multimeter.server.MultimeterServer;

public interface IMinecraftServer {
	
	public MultimeterServer getMultimeterServer();
	
	public boolean isPausedRSMM();
	
}
