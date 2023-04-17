package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;

import redstone.multimeter.common.network.Packets;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

	@Shadow private Minecraft minecraft;

	@Inject(
		method = "handleLogin",
		at = @At(
			value = "RETURN"
		)
	)
	private void handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
		((IMinecraft)minecraft).getMultimeterClient().onConnect();

	}

	@Inject(
		method = "handleCustomPayload",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void handleCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
		if (Packets.getChannel().equals(packet.getIdentifier())) {
			PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, minecraft);
			((IMinecraft)minecraft).getMultimeterClient().getPacketHandler().handlePacket(packet.getData());

			ci.cancel();
		}
	}
}
