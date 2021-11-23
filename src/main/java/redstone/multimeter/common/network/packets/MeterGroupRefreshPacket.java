package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

public class MeterGroupRefreshPacket implements RSMMPacket {
	
	private String name;
	private CompoundTag meterGroupData;
	
	public MeterGroupRefreshPacket() {
		
	}
	
	public MeterGroupRefreshPacket(MeterGroup meterGroup) {
		this.name = meterGroup.getName();
		this.meterGroupData = meterGroup.toNBT();
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.putString("name", name);
		data.method_10566("data", meterGroupData);
	}
	
	@Override
	public void decode(CompoundTag data) {
		name = data.getString("name");
		meterGroupData = data.getCompound("data");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		Multimeter multimeter = server.getMultimeter();
		
		if (multimeter.hasSubscription(player)) {
			multimeter.refreshMeterGroup(player);
		}
	}
	
	@Override
	public void execute(MultimeterClient client) {
		ClientMeterGroup meterGroup = client.getMeterGroup();
		
		if (meterGroup.getName().equals(name)) {
			meterGroup.refresh(meterGroupData);
		} else {
			meterGroup.subscribe(name);
		}
	}
}
