package rsmm.fabric.common.task;

import net.minecraft.network.PacketByteBuf;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.util.PacketUtils;

public class AddMeterTask implements MultimeterTask {
	
	private WorldPos pos;
	private String name;
	private int color;
	private boolean initialPowered;
	private boolean initialActive;
	
	public AddMeterTask() {
		
	}
	
	public AddMeterTask(WorldPos pos, String name, int color, boolean powered, boolean active) {
		this.pos = pos;
		this.name = name;
		this.color = color;
		this.initialPowered = powered;
		this.initialActive = active;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof AddMeterTask) {
			AddMeterTask task = (AddMeterTask)other;
			
			return pos.equals(task.pos);
		}
		
		return false;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		PacketUtils.writeWorldPos(buffer, pos);
		buffer.writeString(name);
		buffer.writeInt(color);
		buffer.writeBoolean(initialPowered);
		buffer.writeBoolean(initialActive);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pos = PacketUtils.readWorldPos(buffer);
		name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		color = buffer.readInt();
		initialPowered = buffer.readBoolean();
		initialActive = buffer.readBoolean();
	}
	
	@Override
	public boolean run(MeterGroup meterGroup) {
		Meter meter = new Meter(pos, name, color, initialPowered, initialActive);
		meterGroup.addMeter(meter);
		
		return true;
	}
}
