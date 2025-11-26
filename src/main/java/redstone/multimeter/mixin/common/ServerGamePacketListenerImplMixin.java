package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import redstone.multimeter.common.network.PacketWrapper;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {

	@Shadow private ServerPlayer player;

	private ServerGamePacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
		super(server, connection, cookie);
	}

	@Inject(
		method = "handleCustomPayload",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	public void rsmm$handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
		if (packet.payload() instanceof PacketWrapper p) {
			PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl)(Object)this, server.packetProcessor());
			((IMinecraftServer)server).getMultimeterServer().getPacketHandler().handlePacket(p, player);

			ci.cancel();
		}
	}
}
