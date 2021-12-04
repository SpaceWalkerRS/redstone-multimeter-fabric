package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.command.AbstractCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandManager;

import redstone.multimeter.command.MeterGroupCommand;

@Mixin(ServerCommandManager.class)
public abstract class ServerCommandManagerMixin extends AbstractCommandManager {
	
	@Inject(
			method="<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void registerCommands(MinecraftServer server, CallbackInfo ci) {
		register(new MeterGroupCommand(server));
	}
}
