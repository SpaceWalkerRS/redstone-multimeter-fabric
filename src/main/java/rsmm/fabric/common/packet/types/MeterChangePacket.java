package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.DimPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.NBTUtils;

public class MeterChangePacket extends AbstractRSMMPacket {
	
	private int meterIndex;
	private CompoundTag properties;
	
	public MeterChangePacket() {
		
	}
	
	public MeterChangePacket(int meterIndex) {
		this.meterIndex = meterIndex;
		this.properties = new CompoundTag();
	}
	
	public void addPos(DimPos pos) {
		properties.put("pos", NBTUtils.dimPosToTag(pos));
	}
	
	public void addName(String name) {
		properties.putString("name", name);
	}
	
	public void addColor(int color) {
		properties.putInt("color", color);
	}
	
	public void addIsMovable(boolean movable) {
		properties.putBoolean("isMovable", movable);
	}
	
	public void addEventType(EventType type) {
		NBTUtils.putEventType(properties, "eventType", type);
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.putInt("meterIndex", meterIndex);
		data.put("properties", properties);
	}
	
	@Override
	public void decode(CompoundTag data) {
		meterIndex = data.getInt("meterIndex");
		properties = data.getCompound("properties");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		Multimeter multimeter = server.getMultimeter();
		
		if (properties.contains("pos")) {
			DimPos pos = NBTUtils.tagToDimPos(properties.getCompound("pos"));
			multimeter.moveMeter(meterIndex, pos, player);
		}
		if (properties.contains("name")) {
			String name = properties.getString("name");
			multimeter.renameMeter(meterIndex, name, player);
		}
		if (properties.contains("color")) {
			int color = properties.getInt("color");
			multimeter.recolorMeter(meterIndex, color, player);
		}
		if (properties.contains("isMovable")) {
			boolean movable = properties.getBoolean("isMovable");
			multimeter.changeMeterMovability(meterIndex, movable, player);
		}
		if (properties.contains("eventType")) {
			EventType type = NBTUtils.getEventType(properties, "eventType");
			multimeter.toggleEventType(meterIndex, type, player);
		}
	}
	
	@Override
	public void execute(MultimeterClient client) {
		
	}
}
