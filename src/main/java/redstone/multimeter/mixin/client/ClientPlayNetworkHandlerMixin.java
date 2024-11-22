package redstone.multimeter.mixin.client;

import java.io.DataInputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.packet.ChatMessagePacket;
import net.minecraft.network.packet.CustomPayloadPacket;
import net.minecraft.network.packet.LoginPacket;

import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IMinecraft;
import redstone.multimeter.util.DataStreams;
import redstone.multimeter.util.TextUtils;

@Mixin(ClientNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Shadow private Minecraft minecraft;

	@Inject(
		method = "handleLogin",
		at = @At(
			value = "RETURN"
		)
	)
	private void handleLogin(LoginPacket packet, CallbackInfo ci) {
		((IMinecraft)minecraft).getMultimeterClient().onConnect();

	}

	@Inject(
		method = "handleChatMessage",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void handleChatMessage(ChatMessagePacket packet, CallbackInfo ci) {
		if (packet.message.startsWith(TextUtils.ACTION_BAR_KEY)) {
			String message = packet.message.substring(TextUtils.ACTION_BAR_KEY.length());
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
	private void handleCustomPayload(CustomPayloadPacket packet, CallbackInfo ci) {
		if (Packets.getChannel().equals(packet.channel)) {
			DataInputStream input = DataStreams.input(packet.data);
			((IMinecraft)minecraft).getMultimeterClient().getPacketHandler().handlePacket(input);

			ci.cancel();
		}
	}
}
