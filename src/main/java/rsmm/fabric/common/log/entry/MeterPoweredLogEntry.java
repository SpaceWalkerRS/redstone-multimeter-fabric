package rsmm.fabric.common.log.entry;

import net.minecraft.network.PacketByteBuf;

public class MeterPoweredLogEntry extends AbstractLogEntry {
	
	private int meterIndex;
	private boolean powered;
	
	public MeterPoweredLogEntry(int meterIndex, boolean powered) {
		this.meterIndex = meterIndex;
		this.powered = powered;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(meterIndex);
		buffer.writeBoolean(powered);
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		meterIndex = buffer.readInt();
		powered = buffer.readBoolean();
	}
	
	public int getMeterIndex() {
		return meterIndex;
	}
	
	public boolean isPowered() {
		return powered;
	}
}
