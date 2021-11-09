package redstone.multimeter.interfaces.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface ICustomPayloadC2SPacket {
	
	public Identifier getPacketChannelRSMM();
	
	public PacketByteBuf getPacketDataRSMM();
	
}
