package rsmm.fabric.common;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class DimPos {
	
	private final Identifier dimensionId;
	private final BlockPos pos;
	
	public DimPos(Identifier dimensionId, BlockPos pos) {
		this.dimensionId = dimensionId;
		this.pos = pos.toImmutable();
	}
	
	public DimPos(World world, BlockPos pos) {
		this(DimensionType.getId(world.dimension.getType()), pos);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DimPos) {
			DimPos dimPos = (DimPos)o;
			
			return dimensionId.equals(dimPos.dimensionId) && pos.equals(dimPos.pos);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return pos.hashCode() + 31 * dimensionId.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", dimensionId.toString(), pos.getX(), pos.getY(), pos.getZ());
	}
	
	public Identifier getDimensionId() {
		return dimensionId;
	}
	
	public boolean isOf(World world) {
		return DimensionType.getId(world.dimension.getType()).equals(dimensionId);
	}
	
	public BlockPos asBlockPos() {
		return pos;
	}
	
	public DimPos offset(Direction dir) {
		return offset(dir, 1);
	}
	
	public DimPos offset(Direction dir, int distance) {
		return new DimPos(dimensionId, pos.offset(dir, distance));
	}
	
	public DimPos offset(int dx, int dy, int dz) {
		return new DimPos(dimensionId, pos.add(dx, dy, dz));
	}
	
	/**
	 * Return a DimPos with the same coordinates in a different dimension
	 */
	public DimPos withDimension(Identifier dimensionId) {
		return new DimPos(dimensionId, pos);
	}
}
