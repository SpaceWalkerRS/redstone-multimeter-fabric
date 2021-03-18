package rsmm.fabric.common.task;

import net.minecraft.network.PacketByteBuf;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.util.PacketUtils;

public class RemoveMeterTask implements MultimeterTask {
	
	private WorldPos pos;
	
	public RemoveMeterTask(WorldPos pos) {
		this.pos = pos;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof RemoveMeterTask) {
			RemoveMeterTask task = (RemoveMeterTask)other;
			
			return pos.equals(task.pos);
		}
		
		return false;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
	}
	
	@Override
	public boolean run(MeterGroup meterGroup) {
		meterGroup.removeMeterAt(pos);
		
		return true;
	}
}
