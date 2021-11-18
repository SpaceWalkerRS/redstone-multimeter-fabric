package redstone.multimeter.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import redstone.multimeter.util.NBTUtils;

public class DimPos {
	
	private final Identifier dimensionId;
	private final BlockPos blockPos;
	
	public DimPos(Identifier dimensionId, BlockPos blockPos) {
		this.dimensionId = dimensionId;
		this.blockPos = blockPos.toImmutable();
	}
	
	public DimPos(Identifier dimensionId, int x, int y, int z) {
		this(dimensionId, new BlockPos(x, y, z));
	}
	
	public DimPos(World world, BlockPos pos) {
		this(DimensionType.getId(world.dimension.getType()), pos);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DimPos) {
			DimPos pos = (DimPos)obj;
			return pos.dimensionId.equals(dimensionId) && pos.blockPos.equals(blockPos);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return blockPos.hashCode() + 31 * dimensionId.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", dimensionId.toString(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public Identifier getDimensionId() {
		return dimensionId;
	}
	
	public boolean isOf(World world) {
		return DimensionType.getId(world.dimension.getType()).equals(dimensionId);
	}
	
	public DimPos offset(Identifier dimensionId) {
		return new DimPos(dimensionId, blockPos);
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public DimPos offset(Direction dir) {
		return offset(dir, 1);
	}
	
	public DimPos offset(Direction dir, int distance) {
		return new DimPos(dimensionId, blockPos.offset(dir, distance));
	}
	
	public DimPos offset(Axis axis) {
		return offset(axis, 1);
	}
	
	public DimPos offset(Axis axis, int distance) {
		int dx = axis.choose(distance, 0, 0);
		int dy = axis.choose(0, distance, 0);
		int dz = axis.choose(0, 0, distance);
		
		return offset(dx, dy, dz);
	}
	
	public DimPos offset(int dx, int dy, int dz) {
		return new DimPos(dimensionId, blockPos.add(dx, dy, dz));
	}
	
	public CompoundTag toNBT() {
		CompoundTag nbt = new CompoundTag();
		
		// The key is "world id" to match RSMM for 1.16+
		// Keeping this key consistent between versions
		// allows clients and servers of different versions
		// to communicate effectively through the use of
		// mods like ViaVersion or multiconnect
		nbt.put("world id", NBTUtils.identifierToNBT(dimensionId));
		nbt.putInt("x", blockPos.getX());
		nbt.putInt("y", blockPos.getY());
		nbt.putInt("z", blockPos.getZ());
		
		return nbt;
	}
	
	public static DimPos fromNBT(CompoundTag nbt) {
		Identifier dimensionId = NBTUtils.NBTToIdentifier(nbt.getCompound("world id"));
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");
		
		return new DimPos(dimensionId, x, y, z);
	}
}
