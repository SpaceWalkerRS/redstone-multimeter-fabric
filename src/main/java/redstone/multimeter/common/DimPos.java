package redstone.multimeter.common;

import com.google.common.base.Objects;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import redstone.multimeter.util.DimensionUtils;
import redstone.multimeter.util.Direction;
import redstone.multimeter.util.Direction.Axis;

public class DimPos {

	private final String dimension;
	private final int x;
	private final int y;
	private final int z;

	public DimPos(String dimension, int x, int y, int z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public DimPos(World world, int x, int y, int z) {
		this(DimensionUtils.getKey(world.dimension), x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DimPos) {
			DimPos other = (DimPos)obj;
			return other.dimension.equals(dimension) && other.x == x && other.y == y && other.z == z;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dimension, x, y, z);
	}

	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", dimension.toString(), x, y, z);
	}

	public String getDimension() {
		return dimension;
	}

	public boolean is(World world) {
		return DimensionUtils.getKey(world.dimension).equals(dimension);
	}

	public DimPos offset(String dimension) {
		return new DimPos(dimension, x, y, z);
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

	public boolean is(int x, int y, int z) {
		return x == this.x && y == this.y && z == this.z;
	}

	public DimPos offset(Direction dir) {
		return offset(dir, 1);
	}

	public DimPos offset(Direction dir, int distance) {
		return offset(distance * dir.getOffsetX(), distance * dir.getOffsetY(), distance * dir.getOffsetZ());
	}

	public DimPos offset(Axis axis) {
		return offset(axis, 1);
	}

	public DimPos offset(Axis axis, int distance) {
		return offset(axis.choose(distance, 0, 0), axis.choose(0, distance, 0), axis.choose(0, 0, distance));
	}

	public DimPos offset(int dx, int dy, int dz) {
		return new DimPos(dimension, x + dx, y + dy, z + dz);
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();

		nbt.putString("dim", dimension);
		nbt.putInt("x", x);
		nbt.putInt("y", y);
		nbt.putInt("z", z);

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
