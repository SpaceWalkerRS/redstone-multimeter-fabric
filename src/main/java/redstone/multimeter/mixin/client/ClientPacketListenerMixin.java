package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;

import redstone.multimeter.common.network.PacketWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {

	private ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
		super(minecraft, connection, cookie);
	}

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
	private void handleCustomPayload(CustomPacketPayload packet, CallbackInfo ci) {
		if (packet instanceof PacketWrapper p) {
			((IMinecraft)minecraft).getMultimeterClient().getPacketHandler().handlePacket(p);
			ci.cancel();
		}
	}
}
