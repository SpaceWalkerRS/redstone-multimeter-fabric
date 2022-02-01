package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

import redstone.multimeter.command.MeterGroupCommand;

@Mixin(ServerCommandManager.class)
public abstract class ServerCommandManagerMixin extends CommandHandler {
	
	@Shadow @Final private MinecraftServer server;
	
	@Inject(
			method="<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void registerCommands(MinecraftServer minecraftServer, CallbackInfo ci) {
		this.registerCommand(new MeterGroupCommand(server));
	}
}
