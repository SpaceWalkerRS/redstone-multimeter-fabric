package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import redstone.multimeter.interfaces.mixin.IServerboundCustomPayloadPacket;

@Mixin(ServerboundCustomPayloadPacket.class)
public class ServerboundCustomPayloadPacketMixin implements IServerboundCustomPayloadPacket {

	@Shadow private ResourceLocation identifier;
	@Shadow private FriendlyByteBuf data;

	@Override
	public ResourceLocation rsmm$getChannel() {
		return identifier;
	}

	@Override
	public FriendlyByteBuf rsmm$getData() {
		return data;
	}
}
