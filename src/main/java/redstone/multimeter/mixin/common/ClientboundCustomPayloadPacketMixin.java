package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.common.network.Packets;

@Mixin(ClientboundCustomPayloadPacket.class)
public class ClientboundCustomPayloadPacketMixin {

	@Inject(
		method = "readPayload",
		cancellable = true,
		at = @At(
			value = "HEAD"
//			can't use this target since FAPI uses it and unconditionally cancels
//			value = "INVOKE",
//			target = "Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;readUnknownPayload(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/common/custom/DiscardedPayload;"
		)
	)
	private static void rsmm$readPayload(ResourceLocation channel, FriendlyByteBuf buffer, CallbackInfoReturnable<CustomPacketPayload> cir) {
		if (Packets.getChannel().equals(channel)) {
			cir.setReturnValue(Packets.READER.apply(buffer));
		}
	}
}
