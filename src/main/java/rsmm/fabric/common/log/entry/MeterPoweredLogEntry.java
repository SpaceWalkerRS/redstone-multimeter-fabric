package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;
import rsmm.fabric.common.log.AbstractLogEntry;

public class MeterPoweredLogEntry extends AbstractLogEntry {
	
	private int meterIndex;
	private boolean powered;
	
	public MeterPoweredLogEntry() {
		
	}
	
	public MeterPoweredLogEntry(int meterIndex, boolean powered) {
		this.meterIndex = meterIndex;
		this.powered = powered;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(meterIndex);
		buffer.writeBoolean(powered);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		meterIndex = buffer.readInt();
		powered = buffer.readBoolean();
	}
	
	@Override
	public void print() {
		System.out.println("meter " + meterIndex + " was " + (powered ? "powered" : "unpowered"));
	}
	
	public int getMeterIndex() {
		return meterIndex;
	}
	
	public boolean isPowered() {
		return powered;
	}
}
