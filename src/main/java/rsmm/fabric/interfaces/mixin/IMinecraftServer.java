package rsmm.fabric.interfaces.mixin;

import rsmm.fabric.server.MultimeterServer;

public interface IMinecraftServer {
	
	public MultimeterServer getMultimeterServer();
	
	public boolean isPaused();
	
}
