package redstone.multimeter.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

import redstone.multimeter.util.DimensionUtils;
import redstone.multimeter.util.Direction;
import redstone.multimeter.util.Direction.Axis;
import redstone.multimeter.util.Identifier;
import redstone.multimeter.util.NbtUtils;

public class DimPos {
	
	private final Identifier dimensionId;
	private final int x;
	private final int y;
	private final int z;
	
	public DimPos(Identifier dimensionId, int x, int y, int z) {
		this.dimensionId = dimensionId;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public DimPos(World world, int x, int y, int z) {
		this(DimensionUtils.getId(world.dimension.dimensionType), x, y, z);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DimPos) {
			DimPos pos = (DimPos)obj;
			return pos.dimensionId.equals(dimensionId) && pos.x == x && pos.y == y && pos.z == z;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return x + 31 * (y + 31 * z) + 31 * dimensionId.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", dimensionId.toString(), x, y, z);
	}
	
	public Identifier getDimensionId() {
		return dimensionId;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public boolean isOf(World world) {
		return DimensionUtils.getId(world.dimension.dimensionType).equals(dimensionId);
	}
	
	public DimPos offset(Identifier dimensionId) {
		return offset(dimensionId, 0, 0, 0);
	}
	
	public DimPos offset(Direction dir) {
		return offset(dir, 1);
	}
	
	public DimPos offset(Direction dir, int dist) {
		return offset(dimensionId, dist * dir.getOffsetX(), dist * dir.getOffsetY(), dist * dir.getOffsetZ());
	}
	
	public DimPos offset(Axis axis) {
		return offset(axis, 1);
	}
	
	public DimPos offset(Axis axis, int dist) {
		return offset(dimensionId, axis.choose(dist, 0, 0), axis.choose(0, dist, 0), axis.choose(0, 0, dist));
	}
	
	public DimPos offset(Identifier dimensionId, int dx, int dy, int dz) {
		return new DimPos(dimensionId, x + dx, y + dy, z + dz);
	}
	
	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();
		
		// The key is "world id" to match RSMM for 1.16+
		// Keeping this key consistent between versions
		// allows clients and servers of different versions
		// to communicate effectively through the use of
		// mods like ViaVersion or multiconnect
		nbt.put("world id", NbtUtils.identifierToNbt(dimensionId));
		nbt.putInt("x", x);
		nbt.putInt("y", y);
		nbt.putInt("z", z);
		
		return nbt;
	}
	
	public static DimPos fromNbt(CompoundTag nbt) {
		Identifier dimensionId = NbtUtils.nbtToIdentifier(nbt.getCompound("world id"));
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");
		
		return new DimPos(dimensionId, x, y, z);
	}
}
