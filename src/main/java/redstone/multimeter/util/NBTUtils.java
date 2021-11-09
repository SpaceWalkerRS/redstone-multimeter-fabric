package redstone.multimeter.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import redstone.multimeter.common.TickPhase;
import redstone.multimeter.common.DimPos;
import redstone.multimeter.common.meter.event.EventType;

public class NBTUtils {
	
	public static void putIdentifier(CompoundTag nbt, String key, Identifier id) {
		nbt.putString(key, id.toString());
	}
	
	public static Identifier getIdentifier(CompoundTag nbt, String key) {
		return nbt.contains(key) ? new Identifier(nbt.getString(key)) : null;
	}
	
	public static void putEventType(CompoundTag nbt, String key, EventType type) {
		nbt.putByte(key, (byte)type.getIndex());
	}
	
	public static EventType getEventType(CompoundTag nbt, String key) {
		return nbt.contains(key) ? EventType.fromIndex(nbt.getByte(key)) : null;
	}
	
	public static void putTickPhase(CompoundTag nbt, String key, TickPhase tickPhase) {
		nbt.putByte(key, (byte)tickPhase.getIndex());
	}
	
	public static TickPhase getTickPhase(CompoundTag nbt, String key) {
		return nbt.contains(key) ? TickPhase.fromIndex(nbt.getByte(key)) : null;
	}
	
	public static CompoundTag dimPosToNBT(DimPos pos) {
		CompoundTag nbt = new CompoundTag();
		
		// The key is "worldId" which matches with RSMM for 1.16+.
		// This allows RSMM to work between different versions 
		// through the use of e.g. ViaVersion or multiconnect.
		putIdentifier(nbt, "worldId", pos.getDimensionId());
		nbt.putInt("x", pos.getBlockPos().getX());
		nbt.putInt("y", pos.getBlockPos().getY());
		nbt.putInt("z", pos.getBlockPos().getZ());
		
		return nbt;
	}
	
	public static DimPos NBTToDimPos(CompoundTag nbt) {
		if (!nbt.contains("worldId")) {
			return null;
		}
		
		// The key is "worldId" which matches with RSMM for 1.16+.
		// This allows RSMM to work between different versions 
		// through the use of e.g. ViaVersion or multiconnect.
		Identifier dimensionId = getIdentifier(nbt, "worldId");
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");
		
		return new DimPos(dimensionId, new BlockPos(x, y, z));
	}
}
