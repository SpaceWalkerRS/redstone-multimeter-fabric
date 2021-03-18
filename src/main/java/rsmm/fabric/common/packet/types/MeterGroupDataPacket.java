package rsmm.fabric.common.packet.types;

import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.PacketUtils;

public class MeterGroupDataPacket extends AbstractRSMMPacket {
	
	private MeterGroup meterGroup;
	
	public MeterGroupDataPacket() {
		
	}
	
	public MeterGroupDataPacket(MeterGroup meterGroup) {
		this.meterGroup = meterGroup;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		List<Meter> meters = meterGroup.getMeters();
		
		buffer.writeString(meterGroup.getName());		
		buffer.writeInt(meters.size());
		
		for (Meter meter : meters) {
			PacketUtils.writeMeter(buffer, meter);
		}
		
		meterGroup.getLogs().encode(buffer);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		String name = buffer.readString(PacketUtils.MAX_STRING_LENGTH);
		int meterCount = buffer.readInt();
		
		meterGroup = new MeterGroup(name);
		
		for (int i = 0; i < meterCount; i++) {
			Meter meter = PacketUtils.readMeter(buffer);
			
			meterGroup.addMeter(meter);
		}
		
		meterGroup.getLogs().decode(buffer);
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.meterGroupDataReceived(meterGroup, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.subscribeToMeterGroup(meterGroup);
	}
}
