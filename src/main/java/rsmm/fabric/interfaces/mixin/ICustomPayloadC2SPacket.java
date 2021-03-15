package rsmm.fabric.interfaces.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface ICustomPayloadC2SPacket {
	
	public Identifier getPacketChannel();
	
	public PacketByteBuf getPacketData();
	
}
