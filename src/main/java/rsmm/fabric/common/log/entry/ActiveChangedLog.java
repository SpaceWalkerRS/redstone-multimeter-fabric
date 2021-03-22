package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;
import rsmm.fabric.common.log.LogType;

public class ActiveChangedLog extends LogEntry {
	
	private boolean active;
	
	/**
	 * Creates an empty log to be populated by packet data
	 */
	public ActiveChangedLog() {
		super(LogType.ACTIVE_CHANGED, -1, -1);
	}
	
	public ActiveChangedLog(long tick, long subTick, boolean active) {
		super(LogType.ACTIVE_CHANGED, tick, subTick);
		this.active = active;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		super.encode(buffer);
		buffer.writeBoolean(active);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		super.decode(buffer);
		active = buffer.readBoolean();
	}
	
	public boolean isActive() {
		return active;
	}
	
	@Override
	public void print() {
		System.out.println(getTick() + "t " + getSubTick() + " - became " + (active ? "active" : "inactive"));
	}
}
