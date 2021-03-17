package rsmm.fabric.common.task;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.MeterGroup;

public class RecolorMeterTask implements MultimeterTask {
	
	private int index;
	private int color;
	
	public RecolorMeterTask(int index, int color) {
		this.index = index;
		this.color = color;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof RecolorMeterTask) {
			RecolorMeterTask task = (RecolorMeterTask)other;
			
			return index == task.index;
		}
		
		return false;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(index);
		buffer.writeInt(color);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		index = buffer.readInt();
		color = buffer.readInt();
	}
	
	@Override
	public void run(MeterGroup meterGroup) {
		meterGroup.recolorMeter(index, color);
	}
}
