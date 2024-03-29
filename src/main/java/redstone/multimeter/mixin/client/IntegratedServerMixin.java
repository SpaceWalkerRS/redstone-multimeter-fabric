package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.server.IntegratedServer;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements IMinecraftServer {

	@Shadow private boolean paused;

	@Override
	public boolean rsmm$isPaused() {
		return paused;
	}
}
