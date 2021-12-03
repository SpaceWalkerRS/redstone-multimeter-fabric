package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerCommandSource;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements IServerCommandSource {
	
	@Shadow @Final private MinecraftServer server;
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
}
