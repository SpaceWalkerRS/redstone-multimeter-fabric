package redstone.multimeter.interfaces.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.Identifier;

public interface ICustomPayloadC2SPacket {

	Identifier rsmm$getChannel();

	PacketByteBuf rsmm$getData();

}
