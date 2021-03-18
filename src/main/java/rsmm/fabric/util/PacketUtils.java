package rsmm.fabric.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.task.MultimeterTask;
import rsmm.fabric.common.task.TaskType;

public class PacketUtils {
	
	public static final int MAX_STRING_LENGTH = 32767;
	
	public static void writeWorldPos(PacketByteBuf buffer, WorldPos pos) {
		buffer.writeIdentifier(pos.getWorldId());
		buffer.writeBlockPos(pos);
	}
	
	public static WorldPos readWorldPos(PacketByteBuf buffer) {
		Identifier worldId = buffer.readIdentifier();
		BlockPos pos = buffer.readBlockPos();
		
		return new WorldPos(worldId, pos);
	}
	
	public static void writeMultimeterTask(PacketByteBuf buffer, MultimeterTask task) {
		TaskType type = TaskType.fromTask(task);
		buffer.writeByte(type.getIndex());
		
		task.encode(buffer);
	}
	
	public static MultimeterTask readMultimeterTask(PacketByteBuf buffer) {
		TaskType type = TaskType.fromIndex(buffer.readByte());
		Class<? extends MultimeterTask> clazz = type.getClazz();
		
		try {
			MultimeterTask task = clazz.newInstance();
			task.decode(buffer);
			
			return task;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public static void writeMeter(PacketByteBuf buffer, Meter meter) {
		meter.encode(buffer);
	}
	
	public static Meter readMeter(PacketByteBuf buffer) {
		WorldPos pos = PacketUtils.readWorldPos(buffer);
		Meter meter = new Meter(pos, null, 0, false, false);
		
		meter.decode(buffer);
		
		return meter.getName() == null ? null : meter;
	}
}
