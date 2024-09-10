package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.LoginS2CPacket;

import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IMinecraft;
import redstone.multimeter.util.TextUtils;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Shadow private Minecraft minecraft;

	@Inject(
		method = "handleLogin",
		at = @At(
			value = "RETURN"
		)
	)
	private void handleLogin(LoginS2CPacket packet, CallbackInfo ci) {
		((IMinecraft)minecraft).getMultimeterClient().onConnect();

	}

	@Inject(
		method = "handleChatMessage",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void handleChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
		if (TextUtils.ACTION_BAR_KEY.equals(packet.getMessage().getContent())) {
			String message = packet.getMessage().getString().substring(TextUtils.ACTION_BAR_KEY.length());
			minecraft.gui.setOverlayMessage(message, false);

			ci.cancel();
		}
	}

	@Inject(
		method = "handleCustomPayload",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void handleCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (Packets.getChannel().equals(packet.getChannel())) {
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getData()));
			((IMinecraft)minecraft).getMultimeterClient().getPacketHandler().handlePacket(buffer);

			ci.cancel();
		}
	}
}
