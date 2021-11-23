package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.common.network.PacketManager;
import redstone.multimeter.interfaces.mixin.ICustomPayloadC2SPacket;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	
	@Shadow @Final private MinecraftServer server;
	@Shadow private ServerPlayerEntity player;
	
	@Inject(
			method = "onCustomPayload",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onCustomPayload(CustomPayloadC2SPacket minecraftPacket, CallbackInfo ci) {
		ICustomPayloadC2SPacket packet = (ICustomPayloadC2SPacket)minecraftPacket;
		
		if (PacketManager.getPacketChannelId().equals(packet.getPacketChannelRSMM())) {
			NetworkThreadUtils.method_11074(minecraftPacket, (ServerPlayNetworkHandler)(Object)this, server);
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().onPacketReceived(packet.getPacketDataRSMM(), player);
			
			ci.cancel();
		}
	}
}
