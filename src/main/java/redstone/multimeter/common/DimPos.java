package redstone.multimeter.common;

import com.google.common.base.Objects;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

import redstone.multimeter.util.AxisUtils;

public class DimPos {

	private final String dimension;
	private final BlockPos pos;

	public DimPos(String dimension, BlockPos pos) {
		this.dimension = dimension;
		this.pos = pos.immutable();
	}

	public DimPos(String dimension, int x, int y, int z) {
		this(dimension, new BlockPos(x, y, z));
	}

	public DimPos(World world, BlockPos pos) {
		this(world.dimension.getType().getKey(), pos);
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

	public String getDimension() {
		return dimension;
	}

	public boolean is(World world) {
		return world.dimension.getType().getKey().equals(dimension);
	}

	public DimPos offset(String dimension) {
		return new DimPos(dimension, pos);
	}

	public BlockPos getBlockPos() {
		return pos;
	}

	public boolean is(BlockPos pos) {
		return pos.equals(this.pos);
	}

	public DimPos offset(Direction dir) {
		return offset(dir, 1);
	}

	public DimPos offset(Direction dir, int distance) {
		return new DimPos(dimension, pos.offset(dir, distance));
	}

	public DimPos offset(Axis axis) {
		return offset(axis, 1);
	}

	public DimPos offset(Axis axis, int distance) {
		int dx = AxisUtils.choose(axis, distance, 0, 0);
		int dy = AxisUtils.choose(axis, 0, distance, 0);
		int dz = AxisUtils.choose(axis, 0, 0, distance);

		return new DimPos(dimension, pos.add(dx, dy, dz));
	}

	public DimPos offset(int dx, int dy, int dz) {
		return new DimPos(dimension, pos.add(dx, dy, dz));
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();

		nbt.putString("dim", dimension);
		nbt.putInt("x", pos.getX());
		nbt.putInt("y", pos.getY());
		nbt.putInt("z", pos.getZ());

		return nbt;
	}

	public static DimPos fromNbt(NbtCompound nbt) {
		String dimension = nbt.getString("dim");
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");

		return new DimPos(dimension, x, y, z);
	}
}
