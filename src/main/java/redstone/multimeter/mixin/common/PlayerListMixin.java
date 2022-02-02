package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;

import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(PlayerList.class)
public class PlayerListMixin {
	
	@Shadow @Final private MinecraftServer server;
	
	@Inject(
			method = "initializeConnectionToPlayer",
			remap = false,
			at = @At(
					value = "RETURN"
			)
	)
	private void onPlayerJoin(NetworkManager connection, EntityPlayerMP player, NetHandlerPlayServer nethandler, CallbackInfo ci) {
		((IMinecraftServer)server).getMultimeterServer().onPlayerJoin(player);
	}
	
	@Inject(
			method = "playerLoggedOut",
			at = @At(
					value = "HEAD"
			)
	)
	private void onPlayerLeave(EntityPlayerMP player, CallbackInfo ci) {
		((IMinecraftServer)server).getMultimeterServer().onPlayerLeave(player);
	}
}
