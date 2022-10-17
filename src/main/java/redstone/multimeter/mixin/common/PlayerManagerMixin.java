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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	
	@Shadow @Final private MinecraftServer server;
	
	@Inject(
		method = "respawnPlayer",
		at = @At(
			value = "RETURN"
		)
	)
	private void onPlayerRespawn(ServerPlayerEntity player, DimensionType dimensionType, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
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
