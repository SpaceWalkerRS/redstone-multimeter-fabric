package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.resource.Identifier;

import redstone.multimeter.interfaces.mixin.ICustomPayloadC2SPacket;

@Mixin(CustomPayloadC2SPacket.class)
public class CustomPayloadC2SPacketMixin implements ICustomPayloadC2SPacket {

	@Shadow private Identifier channel;
	@Shadow private PacketByteBuf data;

	@Override
	public Identifier rsmm$getChannel() {
		return channel;
	}

	@Override
	public PacketByteBuf rsmm$getData() {
		return data;
	}
}
