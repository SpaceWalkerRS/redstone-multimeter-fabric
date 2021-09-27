package rsmm.fabric.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import rsmm.fabric.common.TickPhase;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;

public class NBTUtils {
	
	public static void putIdentifier(NbtCompound nbt, String key, Identifier id) {
		nbt.putString(key, id.toString());
	}
	
	public static Identifier getIdentifier(NbtCompound nbt, String key) {
		return nbt.contains(key) ? new Identifier(nbt.getString(key)) : null;
	}
	
	public static void putEventType(NbtCompound nbt, String key, EventType type) {
		nbt.putByte(key, (byte)type.getIndex());
	}
	
	public static EventType getEventType(NbtCompound nbt, String key) {
		return nbt.contains(key) ? EventType.fromIndex(nbt.getByte(key)) : null;
	}
	
	public static void putTickPhase(NbtCompound nbt, String key, TickPhase tickPhase) {
		nbt.putByte(key, (byte)tickPhase.getIndex());
	}
	
	public static TickPhase getTickPhase(NbtCompound nbt, String key) {
		return nbt.contains(key) ? TickPhase.fromIndex(nbt.getByte(key)) : null;
	}
	
	public static NbtCompound worldPosToNBT(WorldPos pos) {
		NbtCompound nbt = new NbtCompound();
		
		putIdentifier(nbt, "worldId", pos.getWorldId());
		nbt.putInt("x", pos.getBlockPos().getX());
		nbt.putInt("y", pos.getBlockPos().getY());
		nbt.putInt("z", pos.getBlockPos().getZ());
		
		return nbt;
	}
	
	public static WorldPos NBTToWorldPos(NbtCompound nbt) {
		if (!nbt.contains("worldId")) {
			return null;
		}
		
		Identifier worldId = getIdentifier(nbt, "worldId");
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");
		
		return new WorldPos(worldId, new BlockPos(x, y, z));
	}
}
