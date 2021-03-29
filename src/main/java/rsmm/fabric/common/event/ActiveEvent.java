package rsmm.fabric.common.event;

import net.minecraft.network.PacketByteBuf;

public class ActiveEvent extends MeterEvent {
	
	private boolean active;
	
	public ActiveEvent() {
		super(EventType.ACTIVE);
	}
	
	public ActiveEvent(long tick, long subTick, boolean powered) {
		super(EventType.ACTIVE, tick, subTick);
		
		this.active = powered;
	}
	
	@Override
	protected void encodeEvent(PacketByteBuf buffer) {
		buffer.writeBoolean(active);
	}
	
	@Override
	protected void decodeEvent(PacketByteBuf buffer) {
		active = buffer.readBoolean();
	}
	
	public boolean isActive() {
		return active;
	}
}
