package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;

import net.minecraft.network.PacketByteBuf;
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
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getData()));
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().handlePacket(buffer, player);

			ci.cancel();
		}
	}
}
