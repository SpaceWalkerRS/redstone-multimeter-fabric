package redstone.multimeter.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import redstone.multimeter.interfaces.mixin.ICustomPayloadC2SPacket;

@Mixin(CustomPayloadC2SPacket.class)
public class CustomPayloadC2SPacketMixin implements ICustomPayloadC2SPacket {
	
	@Shadow private String channel;
	@Shadow private PacketByteBuf payload;
	
	@Override
	public Identifier getPacketChannelRSMM() {
		return new Identifier(channel);
	}
	
	@Override
	public PacketByteBuf getPacketDataRSMM() {
		return new PacketByteBuf(payload);
	}
}
