package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

	@Shadow @Final private MinecraftServer server;

	@Inject(
		method = "respawn",
		at = @At(
			value = "TAIL"
		)
	)
	private void onPlayerRespawn(ServerPlayerEntity player, int dimension, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
		((IMinecraftServer)server).getMultimeterServer().getPlayerList().respawn(cir.getReturnValue());
	}

	@Inject(
		method = "remove",
		at = @At(
			value = "HEAD"
		)
	)
	private void onPlayerLeave(ServerPlayerEntity player, CallbackInfo ci) {
		((IMinecraftServer)server).getMultimeterServer().getPlayerList().remove(player);
	}
}
