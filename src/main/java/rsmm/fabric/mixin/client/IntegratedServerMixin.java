package rsmm.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.integrated.IntegratedServer;

import rsmm.fabric.interfaces.mixin.IMinecraftServer;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements IMinecraftServer {
	
	@Shadow private boolean paused;
	
	@Override
	public boolean isPaused() {
		return paused;
	}
}
