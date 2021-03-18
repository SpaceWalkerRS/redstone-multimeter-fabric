package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;
import rsmm.fabric.common.log.AbstractLogEntry;

public class MeterActiveLogEntry extends AbstractLogEntry {
	
	private int meterIndex;
	private boolean active;
	
	public MeterActiveLogEntry() {
		
	}
	
	public MeterActiveLogEntry(int meterIndex, boolean active) {
		this.meterIndex = meterIndex;
		this.active = active;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(meterIndex);
		buffer.writeBoolean(active);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		meterIndex = buffer.readInt();
		active = buffer.readBoolean();
	}
	
	@Override
	public void print() {
		System.out.println("meter " + meterIndex + " became " + (active ? "active" : "inactive"));
	}
	
	public int getMeterIndex() {
		return meterIndex;
	}
	
	public boolean isActive() {
		return active;
	}
}
