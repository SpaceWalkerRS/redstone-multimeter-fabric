package redstone.multimeter.interfaces.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public interface ICustomPayloadC2SPacket {
	
	public Identifier getPacketChannelRSMM();
	
	public PacketByteBuf getPacketDataRSMM();
	
}
