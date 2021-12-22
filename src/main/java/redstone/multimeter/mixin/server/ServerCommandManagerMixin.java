package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandRegistry;

import redstone.multimeter.command.MeterGroupCommand;

@Mixin(CommandManager.class)
public abstract class ServerCommandManagerMixin extends CommandRegistry {
	
	@Inject(
			method="<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void registerCommands(MinecraftServer server, CallbackInfo ci) {
		registerCommand(new MeterGroupCommand(server));
	}
}
