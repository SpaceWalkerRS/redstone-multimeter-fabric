package redstone.multimeter.interfaces.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IServerboundCustomPayloadPacket {

	ResourceLocation rsmm$getChannel();

	FriendlyByteBuf rsmm$getData();

}
