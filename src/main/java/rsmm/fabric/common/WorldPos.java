package rsmm.fabric.common;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WorldPos extends BlockPos {
	
	private final Identifier worldId;
	
	public WorldPos(Identifier worldId, int x, int y, int z) {
		super(x, y, z);
		
		this.worldId = worldId;
	}
	
	public WorldPos(Identifier worldId, BlockPos pos) {
		this(worldId, pos.getX(), pos.getY(), pos.getZ());
	}
	
	public WorldPos(World world, BlockPos pos) {
		this(world.getRegistryKey().getValue(), pos);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof WorldPos) {
			WorldPos pos = (WorldPos)o;
			
			return worldId.equals(pos.worldId) && getX() == pos.getX() && getY() == pos.getY() && getZ() == pos.getZ();
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + 31 * worldId.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s{%d,%d,%d}", worldId.toString(), getX(), getY(), getZ());
	}
	
	public Identifier getWorldId() {
		return worldId;
	}
	
	public boolean isOf(World world) {
		return world.getRegistryKey().getValue().equals(worldId);
	}
	
	public WorldPos offset(Direction dir) {
		return new WorldPos(worldId, super.offset(dir));
	}
	
	/**
	 * Return a WorldPos with the same coordinates in a different dimension
	 */
	public WorldPos withWorld(Identifier worldId) {
		return new WorldPos(worldId, this);
	}
}
