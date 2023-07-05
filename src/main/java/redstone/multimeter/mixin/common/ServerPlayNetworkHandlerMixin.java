package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.PacketUtils;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;

import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

	@Shadow @Final private MinecraftServer server;
	@Shadow private ServerPlayerEntity player;

	@Inject(
		method = "handleCustomPayload",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void handleCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (Packets.getChannel().equals(packet.getChannel())) {
			PacketUtils.ensureOnSameThread(packet, (ServerPlayNetworkHandler)(Object)this, server);
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().handlePacket(packet.getData(), player);

			ci.cancel();
		}
	}
}
