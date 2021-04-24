package rsmm.fabric.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.common.DimPos;
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
	
	public static CompoundTag dimPosToTag(DimPos pos) {
		CompoundTag tag = new CompoundTag();
		
		putIdentifier(tag, "dimensionId", pos.getDimensionId());
		tag.putInt("x", pos.getBlockPos().getX());
		tag.putInt("y", pos.getBlockPos().getY());
		tag.putInt("z", pos.getBlockPos().getZ());
		
		return tag;
	}
	
	public static DimPos tagToDimPos(CompoundTag tag) {
		if (!tag.contains("dimensionId")) {
			return null;
		}
		
		Identifier worldId = getIdentifier(tag, "dimensionId");
		int x = tag.getInt("x");
		int y = tag.getInt("y");
		int z = tag.getInt("z");
		
		return new DimPos(worldId, new BlockPos(x, y, z));
	}
}