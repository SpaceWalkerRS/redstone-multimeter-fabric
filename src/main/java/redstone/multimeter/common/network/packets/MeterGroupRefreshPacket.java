package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.meter.ClientMeterGroup;
import redstone.multimeter.common.meter.MeterGroup;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.Multimeter;
import redstone.multimeter.server.MultimeterServer;

public class MeterGroupRefreshPacket implements RSMMPacket {
	
	private String name;
	private NbtCompound meterGroupData;
	
	public MeterGroupRefreshPacket() {
		
	}
	
	public MeterGroupRefreshPacket(MeterGroup meterGroup) {
		this.name = meterGroup.getName();
		this.meterGroupData = meterGroup.toNBT();
	}
	
	@Override
	public void encode(NbtCompound data) {
		data.putString("name", name);
		data.put("data", meterGroupData);
	}
	
	@Override
	public void decode(NbtCompound data) {
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
