package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.dimension.DimensionType;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(PlayerList.class)
public class PlayerListMixin {

	@Shadow @Final private MinecraftServer server;

	@Inject(
		method = "respawn",
		at = @At(
			value = "TAIL"
		)
	)
	private void onPlayerRespawn(ServerPlayer player, DimensionType dimension, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
		((IMinecraftServer)server).getMultimeterServer().getPlayerList().respawn(cir.getReturnValue());
	}

	@Inject(
		method = "remove",
		at = @At(
			value = "HEAD"
		)
	)
	private void onPlayerLeave(ServerPlayer player, CallbackInfo ci) {
		((IMinecraftServer)server).getMultimeterServer().getPlayerList().remove(player);
	}
}
