package redstone.multimeter.common;

import com.google.common.base.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import redstone.multimeter.util.NbtUtils;

public class DimPos {

	private final ResourceLocation dimension;
	private final BlockPos pos;

	public DimPos(ResourceLocation dimension, BlockPos pos) {
		this.dimension = dimension;
		this.pos = pos.immutable();
	}

	public DimPos(ResourceLocation dimension, int x, int y, int z) {
		this(dimension, new BlockPos(x, y, z));
	}

	public DimPos(Level level, BlockPos pos) {
		this(level.dimension().location(), pos);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DimPos) {
			DimPos other = (DimPos)obj;
			return other.dimension.equals(dimension) && other.pos.equals(pos);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dimension, pos);
	}

	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", dimension.toString(), pos.getX(), pos.getY(), pos.getZ());
	}

	public ResourceLocation getDimension() {
		return dimension;
	}

	public boolean is(Level level) {
		return level.dimension().location().equals(dimension);
	}

	public DimPos relative(ResourceLocation dimension) {
		return new DimPos(dimension, pos);
	}

	public BlockPos getBlockPos() {
		return pos;
	}

	public boolean is(BlockPos pos) {
		return pos.equals(this.pos);
	}

	public DimPos relative(Direction dir) {
		return relative(dir, 1);
	}

	public DimPos relative(Direction dir, int distance) {
		return new DimPos(dimension, pos.relative(dir, distance));
	}

	public DimPos relative(Axis axis) {
		return relative(axis, 1);
	}

	public DimPos relative(Axis axis, int distance) {
		return new DimPos(dimension, pos.relative(axis, distance));
	}

	public DimPos offset(int dx, int dy, int dz) {
		return new DimPos(dimension, pos.offset(dx, dy, dz));
	}

	public CompoundTag toNbt() {
		CompoundTag nbt = new CompoundTag();

		nbt.put("dim", NbtUtils.resourceLocationToNbt(dimension));
		nbt.putInt("x", pos.getX());
		nbt.putInt("y", pos.getY());
		nbt.putInt("z", pos.getZ());

		return nbt;
	}

	public static DimPos fromNbt(CompoundTag nbt) {
		ResourceLocation dimension = NbtUtils.nbtToResourceLocation(nbt.getCompound("dim"));
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");

		return new DimPos(dimension, x, y, z);
	}
}
