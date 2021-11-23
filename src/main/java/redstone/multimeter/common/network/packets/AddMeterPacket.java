package redstone.multimeter.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.common.meter.MeterProperties;
import redstone.multimeter.common.network.RSMMPacket;
import redstone.multimeter.server.MultimeterServer;

public class AddMeterPacket implements RSMMPacket {
	
	private MeterProperties properties;
	
	public AddMeterPacket() {
		
	}
	
	public AddMeterPacket(MeterProperties properties) {
		this.properties = properties;
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.method_10566("properties", properties.toNBT());
	}
	
	@Override
	public void decode(CompoundTag data) {
		properties = MeterProperties.fromNBT(data.getCompound("properties"));
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().addMeter(player, properties);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
