package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;
import rsmm.fabric.common.log.LogType;

public class PoweredChangedLog extends LogEntry {
	
	private boolean powered;
	
	/**
	 * Creates an empty log to be populated with packet data
	 */
	public PoweredChangedLog() {
		super(LogType.POWERED_CHANGED, -1, -1);
	}
	
	public PoweredChangedLog(long tick, long subTick, boolean powered) {
		super(LogType.POWERED_CHANGED, tick, subTick);
		this.powered = powered;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		super.encode(buffer);
		buffer.writeBoolean(powered);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		super.decode(buffer);
		powered = buffer.readBoolean();
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	@Override
	public void print() {
		System.out.println(getTick() + "t " + getSubTick() + " - became " + (powered ? "powered" : "unpowered"));
	}
}
