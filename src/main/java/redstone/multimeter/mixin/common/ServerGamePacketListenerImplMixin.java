package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

	@Shadow @Final private MinecraftServer server;
	@Shadow private ServerPlayer player;

	@Inject(
		method = "handleCustomPayload",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
		if (Packets.getChannel().equals(packet.getIdentifier())) {
			PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl)(Object)this, server);
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().handlePacket(packet.getData(), player);

			ci.cancel();
		}
	}
}
