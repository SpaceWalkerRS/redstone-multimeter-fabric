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
		this.pos = pos;
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
	
	public Identifier getDimensionId() {
		return dimensionId;
	}
	
	public boolean isOf(World world) {
		return DimensionType.getId(world.dimension.getType()).equals(dimensionId);
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
	public DimPos withWorld(Identifier dimensionId) {
		return new DimPos(dimensionId, pos);
	}
}
