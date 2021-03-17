package rsmm.fabric.common.task;

import net.minecraft.network.PacketByteBuf;

import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class ToggleMeterTask implements MultimeterTask {
	
	private MultimeterServer server;
	private WorldPos pos;
	
	// This task can only be handled on the server
	public ToggleMeterTask(MultimeterServer server, WorldPos pos) {
		this.server = server;
		this.pos = pos;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ToggleMeterTask) {
			ToggleMeterTask task = (ToggleMeterTask)other;
			
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
	public void run(MeterGroup meterGroup) {
		
	}
}
