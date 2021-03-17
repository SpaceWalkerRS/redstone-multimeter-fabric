package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import rsmm.fabric.interfaces.mixin.IMinecraftServer;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerWorld {
	
	@Shadow public abstract MinecraftServer getServer();
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)getServer()).getMultimeterServer();
	}
}
