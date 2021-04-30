package rsmm.fabric.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import rsmm.fabric.common.TickPhase;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;

public class NBTUtils {
	
	public static void putIdentifier(CompoundTag tag, String key, Identifier id) {
		tag.putString(key, id.toString());
	}
	
	public static Identifier getIdentifier(CompoundTag tag, String key) {
		return tag.contains(key) ? new Identifier(tag.getString(key)) : null;
	}
	
	public static void putEventType(CompoundTag tag, String key, EventType type) {
		tag.putByte(key, (byte)type.getIndex());
	}
	
	public static EventType getEventType(CompoundTag tag, String key) {
		return tag.contains(key) ? EventType.fromIndex(tag.getByte(key)) : null;
	}
	
	public static void putTickPhase(CompoundTag tag, String key, TickPhase tickPhase) {
		tag.putByte(key, (byte)tickPhase.getIndex());
	}
	
	public static TickPhase getTickPhase(CompoundTag tag, String key) {
		return tag.contains(key) ? TickPhase.fromIndex(tag.getByte(key)) : null;
	}
	
	public static CompoundTag worldPosToTag(WorldPos pos) {
		CompoundTag tag = new CompoundTag();
		
		putIdentifier(tag, "worldId", pos.getWorldId());
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
		
		return tag;
	}
	
	public static WorldPos tagToWorldPos(CompoundTag tag) {
		if (!tag.contains("worldId")) {
			return null;
		}
		
		Identifier worldId = getIdentifier(tag, "worldId");
		int x = tag.getInt("x");
		int y = tag.getInt("y");
		int z = tag.getInt("z");
		
		return new WorldPos(worldId, x, y, z);
	}
}
