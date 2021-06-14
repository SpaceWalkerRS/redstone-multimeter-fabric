package rsmm.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

import rsmm.fabric.common.network.PacketManager;
import rsmm.fabric.interfaces.mixin.IMinecraftClient;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	
	@Shadow private MinecraftClient client;
	
	@Inject(
			method = "onCustomPayload",
			cancellable = true,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"
			)
	)
	private void onOnCustomPayloadInjectAfterForceMainThread(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (PacketManager.getPacketChannelId().equals(packet.getChannel())) {
			NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler)(Object)this, client);
			((IMinecraftClient)client).getMultimeterClient().getPacketHandler().onPacketReceived(packet.getData());
			
			ci.cancel();
		}
	}
}
