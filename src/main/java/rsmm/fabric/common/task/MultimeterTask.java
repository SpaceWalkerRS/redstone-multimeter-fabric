package rsmm.fabric.common.task;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.MeterGroup;

public interface MultimeterTask {
	
	public void encode(PacketByteBuf buffer);
	
	public void decode(PacketByteBuf buffer);
	
	public boolean run(MeterGroup meterGroup);
	
}
