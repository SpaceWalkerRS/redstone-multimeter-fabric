package rsmm.fabric.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DimPos {
	
	private final int dimensionId;
	private final BlockPos pos;
	
	public DimPos(int dimensionId, BlockPos pos) {
		this.dimensionId = dimensionId;
		this.pos = pos;
	}
	
	public DimPos(World world, BlockPos pos) {
		this(world.dimension.getType().getRawId(), pos);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DimPos) {
			DimPos dimPos = (DimPos)o;
			
			return dimensionId == dimPos.dimensionId && pos.equals(dimPos.pos);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + 31 * dimensionId;
	}
	
	public int getDimensionId() {
		return dimensionId;
	}
	
	public boolean isOf(World world) {
		return world.dimension.getType().getRawId() == dimensionId;
	}
	
	public BlockPos getBlockPos() {
		return pos;
	}
	
	public DimPos offset(Direction dir) {
		return new DimPos(dimensionId, pos.offset(dir));
	}
	
	/**
	 * Return a DimPos with the same coordinates in a different dimension
	 */
	public DimPos withWorld(int dimensionId) {
		return new DimPos(dimensionId, pos);
	}
}
