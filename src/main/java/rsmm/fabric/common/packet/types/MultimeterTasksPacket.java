package rsmm.fabric.common.packet.types;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.common.task.MultimeterTask;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class MultimeterTasksPacket extends AbstractRSMMPacket {
	
	private List<MultimeterTask> tasks;
	
	public MultimeterTasksPacket() {
		
	}
	
	public MultimeterTasksPacket(List<MultimeterTask> tasks) {
		this.tasks = tasks;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(tasks.size());
		
		for (MultimeterTask task : tasks) {
			PacketUtils.writeMultimeterTask(buffer, task);
		}
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		int taskCount = buffer.readInt();
		
		tasks = new LinkedList<>();
		
		for (int i = 0; i < taskCount; i++) {
			MultimeterTask task = PacketUtils.readMultimeterTask(buffer);
			
			if (task != null) {
				tasks.add(task);
			}
		}
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.syncMultimeterTasks(tasks);
	}
}
