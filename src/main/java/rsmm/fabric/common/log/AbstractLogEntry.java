package rsmm.fabric.common.log;

import net.minecraft.network.PacketByteBuf;

public abstract class AbstractLogEntry {
	
	public abstract void encode(PacketByteBuf buffer);
	
	public abstract void decode(PacketByteBuf buffer);
	
	public abstract void print();
	
}
