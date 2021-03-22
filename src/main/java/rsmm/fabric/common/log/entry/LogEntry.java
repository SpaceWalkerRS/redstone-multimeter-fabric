package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;
import rsmm.fabric.common.log.LogType;

public class LogEntry implements Comparable<LogEntry> {
	
	private final LogType type;
	
	private long tick;
	private long subTick;
	
	protected LogEntry(LogType type, long tick, long subTick) {
		this.type = type;
		this.tick = tick;
		this.subTick = subTick;
	}
	
	/**
	 * @param log1 - the first LogEntry to be compared
	 * @param log2 - the second LogEntry to be compared
	 * @return	the value {@code 0} if both logs have the same time;
	 * 			a value less than {@code 1} if {@code log1} has an earlier time than {@code log2};
	 * 			a value more than {@code 1} if {@code log1} has a later time than {@code log2};
	 */
	public static int compare(LogEntry log1, LogEntry log2) {
		return log1.compareTo(log2);
	}
	
	@Override
	public int compareTo(LogEntry log) {
		int c = Long.compare(tick, log.tick);
		
		return c == 0 ? Long.compare(subTick, log.subTick) : c;
	}
	
	public LogType getType() {
		return type;
	}
	
	public long getTick() {
		return tick;
	}
	
	public long getSubTick() {
		return subTick;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeLong(tick);
		buffer.writeLong(subTick);
	}
	
	public void decode(PacketByteBuf buffer) {
		tick = buffer.readLong();
		subTick = buffer.readLong();
	}
	
	public void print() {
		
	}
}
