package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;

public abstract class AbstractLogEntry {
	
	public abstract void write(PacketByteBuf buffer);
	
	public abstract void read(PacketByteBuf buffer);
	
}
