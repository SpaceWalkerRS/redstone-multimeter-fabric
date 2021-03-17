package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;

public class MeterActiveLogEntry extends AbstractLogEntry {
	
	private final int meterIndex;
	private final boolean active;
	
	public MeterActiveLogEntry(int meterIndex, boolean active) {
		this.meterIndex = meterIndex;
		this.active = active;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		
	}
	
	public int getMeterIndex() {
		return meterIndex;
	}
	
	public boolean isActive() {
		return active;
	}
}
