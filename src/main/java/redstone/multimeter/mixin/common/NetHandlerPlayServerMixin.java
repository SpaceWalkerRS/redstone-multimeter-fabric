package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.server.MinecraftServer;

import redstone.multimeter.common.network.PacketManager;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(NetHandlerPlayServer.class)
public class NetHandlerPlayServerMixin {
	
	@Shadow @Final private MinecraftServer server;
	@Shadow private EntityPlayerMP player;
	
	@Inject(
			method = "processCustomPayload",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onCustomPayload(CPacketCustomPayload packet, CallbackInfo ci) {
		if (PacketManager.getPacketChannelId().equals(packet.getChannelName())) {
			PacketThreadUtil.checkThreadAndEnqueue(packet, (NetHandlerPlayServer)(Object)this, server);
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().onPacketReceived(packet.getBufferData(), player);
			
			ci.cancel();
		}
	}
}
