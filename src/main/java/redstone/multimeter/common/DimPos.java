package redstone.multimeter.common;

import com.google.common.base.Objects;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import redstone.multimeter.util.NbtUtils;

public class DimPos {

	private final Identifier dimension;
	private final BlockPos pos;

	public DimPos(Identifier dimension, BlockPos pos) {
		this.dimension = dimension;
		this.pos = pos.immutable();
	}

	public DimPos(Identifier dimension, int x, int y, int z) {
		this(dimension, new BlockPos(x, y, z));
	}

	public DimPos(World world, BlockPos pos) {
		this(DimensionType.getKey(world.dimension.getType()), pos);
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

	public Identifier getDimension() {
		return dimension;
	}

	public boolean is(World world) {
		return DimensionType.getKey(world.dimension.getType()).equals(dimension);
	}

	public DimPos offset(Identifier dimension) {
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
		int dx = axis.choose(distance, 0, 0);
		int dy = axis.choose(0, distance, 0);
		int dz = axis.choose(0, 0, distance);

		return new DimPos(dimension, pos.add(dx, dy, dz));
	}

	public DimPos offset(int dx, int dy, int dz) {
		return new DimPos(dimension, pos.add(dx, dy, dz));
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();

		nbt.put("dim", NbtUtils.identifierToNbt(dimension));
		nbt.putInt("x", pos.getX());
		nbt.putInt("y", pos.getY());
		nbt.putInt("z", pos.getZ());

		return nbt;
	}

	public static DimPos fromNbt(NbtCompound nbt) {
		Identifier dimension = NbtUtils.nbtToIdentifier(nbt.getCompound("dim"));
		int x = nbt.getInt("x");
		int y = nbt.getInt("y");
		int z = nbt.getInt("z");

		return new DimPos(dimension, x, y, z);
	}
}
