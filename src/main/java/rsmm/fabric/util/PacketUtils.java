package rsmm.fabric.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.log.AbstractLogEntry;
import rsmm.fabric.common.log.entry.LogType;
import rsmm.fabric.common.task.MultimeterTask;
import rsmm.fabric.common.task.TaskType;

public class PacketUtils {
	
	public static final int MAX_STRING_LENGTH = 32767;
	
	public static void writeData(PacketByteBuf buffer, PacketByteBuf data) {
		int bytesCount = data.readableBytes();
		
		buffer.writeInt(bytesCount);
		buffer.writeBytes(data);
	}
	
	public static PacketByteBuf readData(PacketByteBuf buffer) {
		int bytesCount = buffer.readInt();
		PacketByteBuf data = new PacketByteBuf(buffer.readBytes(bytesCount));
		
		return data;
	}
	
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
		
		try {
			Class<? extends MultimeterTask> clazz = type.getClazz();
			MultimeterTask task = clazz.newInstance();
			
			task.decode(buffer);
			
			return task;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public static void writeLogEntry(PacketByteBuf buffer, AbstractLogEntry logEntry) {
		LogType type = LogType.fromLogEntry(logEntry);
		buffer.writeByte(type.getIndex());
		
		logEntry.encode(buffer);
	}
	
	public static AbstractLogEntry readLogEntry(PacketByteBuf buffer) {
		LogType type = LogType.fromIndex(buffer.readByte());
		
		try {
			Class<? extends AbstractLogEntry> clazz = type.getClazz();
			AbstractLogEntry logEntry = clazz.newInstance();
			
			logEntry.decode(buffer);
			
			return logEntry;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public static void writeMeter(PacketByteBuf buffer, Meter meter) {
		PacketUtils.writeWorldPos(buffer, meter.getPos());
		
		meter.encode(buffer);
	}
	
	public static Meter readMeter(PacketByteBuf buffer) {
		WorldPos pos = PacketUtils.readWorldPos(buffer);
		Meter meter = new Meter(pos, null, 0, false, false);
		
		meter.decode(buffer);
		
		return meter.getName() == null ? null : meter;
	}
}
