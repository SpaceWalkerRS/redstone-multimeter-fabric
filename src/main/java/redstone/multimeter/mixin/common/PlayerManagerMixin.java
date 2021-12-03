package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	
	@Shadow @Final private MinecraftServer server;
	
	@Inject(
			method = "onPlayerConnect",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		((IMinecraftServer)server).getMultimeterServer().onPlayerJoin(player);
	}
	
	@Inject(
			method = "remove",
			at = @At(
					value = "HEAD"
			)
	)
	private void onPlayerLeave(ServerPlayerEntity player, CallbackInfo ci) {
		((IMinecraftServer)server).getMultimeterServer().onPlayerLeave(player);
	}
}
