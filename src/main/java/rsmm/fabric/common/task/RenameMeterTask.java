package rsmm.fabric.common.task;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.util.PacketUtils;

public class RenameMeterTask implements MultimeterTask {
	
	private int index;
	private String name;
	
	public RenameMeterTask(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof RenameMeterTask) {
			RenameMeterTask task = (RenameMeterTask)other;
			
			return index == task.index;
		}
		
		return false;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(index);
		buffer.writeString(name);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		index = buffer.readInt();
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
	}
	
	@Override
	public boolean run(MeterGroup meterGroup) {
		meterGroup.renameMeter(index, name);
		
		return true;
	}
}
