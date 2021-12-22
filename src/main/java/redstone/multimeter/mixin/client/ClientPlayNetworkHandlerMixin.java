package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;

import redstone.multimeter.common.network.PacketManager;
import redstone.multimeter.interfaces.mixin.IMinecraftClient;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	
	@Shadow private MinecraftClient client;
	
	@Inject(
			method = "onGameJoin",
			at = @At(
					value = "RETURN"
			)
	)
	private void onGameJoin(GameJoinS2CPacket gameJoinPacket, CallbackInfo ci) {
		((IMinecraftClient)client).getMultimeterClient().onConnect();
		
	}
	
	@Inject(
			method = "onCustomPayload",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void handleCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (PacketManager.getPacketChannelId().toString().equals(packet.getChannel())) {
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(packet.method_7734()));
			((IMinecraftClient)client).getMultimeterClient().getPacketHandler().onPacketReceived(buffer);
			
			ci.cancel();
		}
	}
}
