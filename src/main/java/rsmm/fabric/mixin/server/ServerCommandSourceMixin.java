package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import rsmm.fabric.interfaces.mixin.IServerCommandSource;
import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.MultimeterServer;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements IServerCommandSource {
	
	@Shadow @Final private ServerWorld world;
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IServerWorld)world).getMultimeterServer();
	}
}
