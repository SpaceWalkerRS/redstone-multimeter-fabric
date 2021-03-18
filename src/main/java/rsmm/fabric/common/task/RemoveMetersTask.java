package rsmm.fabric.common.task;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.MeterGroup;

public class RemoveMetersTask implements MultimeterTask {
	
	public RemoveMetersTask() {
		
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof RemoveMetersTask;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		
	}
	
	@Override
	public boolean run(MeterGroup meterGroup) {
		meterGroup.removeMeters();
		
		return true;
	}
}
