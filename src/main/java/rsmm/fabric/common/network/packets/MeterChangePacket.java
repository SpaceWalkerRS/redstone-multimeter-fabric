package rsmm.fabric.common.network.packets;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.network.RSMMPacket;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;
import rsmm.fabric.util.NBTUtils;

public class MeterChangePacket implements RSMMPacket {
	
	private int meterIndex;
	private NbtCompound properties;
	
	public MeterChangePacket() {
		
	}
	
	public MeterChangePacket(int meterIndex) {
		this.meterIndex = meterIndex;
		this.properties = new NbtCompound();
	}
	
	public void addPos(WorldPos pos) {
		properties.put("pos", NBTUtils.worldPosToNBT(pos));
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
	public void encode(NbtCompound data) {
		data.putInt("meterIndex", meterIndex);
		data.put("properties", properties);
	}
	
	@Override
	public void decode(NbtCompound data) {
		meterIndex = data.getInt("meterIndex");
		properties = data.getCompound("properties");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		Multimeter multimeter = server.getMultimeter();
		
		if (properties.contains("pos")) {
			WorldPos pos = NBTUtils.NBTToWorldPos(properties.getCompound("pos"));
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
