package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;

import redstone.multimeter.interfaces.mixin.IServerPacketListener;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin implements IServerPacketListener {

	@Shadow @Final private MinecraftServer server;

	@Inject(
		method = "handleCustomPayload",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	public void rsmm$handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
		PacketUtils.ensureRunningOnSameThread(packet, (ServerCommonPacketListener) this, server);

		if (rsmm$handleCustomPayload(packet.payload())) {
			ci.cancel();
		}
	}

	@Override
	public boolean rsmm$handleCustomPayload(CustomPacketPayload payload) {
		return false;
	}
}
