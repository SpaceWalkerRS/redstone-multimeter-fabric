package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

import redstone.multimeter.common.network.PacketManager;
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
	private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (PacketManager.getPacketChannelId().toString().equals(packet.getChannel())) {
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(packet.method_7981()));
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().onPacketReceived(buffer, player);
			
			ci.cancel();
		}
	}
}
