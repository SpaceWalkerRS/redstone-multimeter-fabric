package redstone.multimeter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketJoinGame;

import redstone.multimeter.common.network.PacketManager;
import redstone.multimeter.interfaces.mixin.IMinecraft;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {
	
	@Shadow private Minecraft client;
	
	@Inject(
			method = "handleJoinGame",
			at = @At(
					value = "RETURN"
			)
	)
	private void onGameJoin(SPacketJoinGame gameJoinPacket, CallbackInfo ci) {
		((IMinecraft)client).getMultimeterClient().onConnect();
		
	}
	
	@Inject(
			method = "handleCustomPayload",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void handleCustomPayload(SPacketCustomPayload packet, CallbackInfo ci) {
		if (PacketManager.getPacketChannelId().equals(packet.getChannelName())) {
			PacketThreadUtil.checkThreadAndEnqueue(packet, (NetHandlerPlayClient)(Object)this, client);
			((IMinecraft)client).getMultimeterClient().getPacketHandler().onPacketReceived(packet.getBufferData());
			
			ci.cancel();
		}
	}
}
