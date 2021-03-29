package rsmm.fabric.common.event;

import net.minecraft.network.PacketByteBuf;

public class PoweredEvent extends MeterEvent {
	
	private boolean powered;
	
	public PoweredEvent() {
		super(EventType.POWERED);
	}
	
	public PoweredEvent(long tick, long subTick, boolean powered) {
		super(EventType.POWERED, tick, subTick);
		
		this.powered = powered;
	}
	
	@Override
	protected void encodeEvent(PacketByteBuf buffer) {
		buffer.writeBoolean(powered);
	}
	
	@Override
	protected void decodeEvent(PacketByteBuf buffer) {
		powered = buffer.readBoolean();
	}
	
	public boolean isPowered() {
		return powered;
	}
}
