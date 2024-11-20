package redstone.multimeter.mixin.common;

import java.io.DataInput;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.CustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;

import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.util.DataStreams;

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
	private void handleCustomPayload(CustomPayloadPacket packet, CallbackInfo ci) {
		if (Packets.getChannel().equals(packet.channel)) {
			DataInput input = DataStreams.input(packet.data);
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().handlePacket(input, player);

			ci.cancel();
		}
	}
}
