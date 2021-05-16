package rsmm.fabric.common;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WorldPos {
	
	private final Identifier worldId;
	private final BlockPos pos;
	
	public WorldPos(Identifier worldId, BlockPos pos) {
		this.worldId = worldId;
		this.pos = pos.toImmutable();
	}
	
	public WorldPos(World world, BlockPos pos) {
		this(world.getRegistryKey().getValue(), pos);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof WorldPos) {
			WorldPos worldPos = (WorldPos)o;
			
			return worldId.equals(worldPos.worldId) && pos.equals(worldPos.pos);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return pos.hashCode() + 31 * worldId.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", worldId.toString(), pos.getX(), pos.getY(), pos.getZ());
	}
	
	public Identifier getWorldId() {
		return worldId;
	}
	
	public boolean isOf(World world) {
		return world.getRegistryKey().getValue().equals(worldId);
	}
	
	public BlockPos asBlockPos() {
		return pos;
	}
	
	public WorldPos offset(Direction dir) {
		return new WorldPos(worldId, pos.offset(dir));
	}
	
	/**
	 * Return a WorldPos with the same coordinates in a different dimension
	 */
	public WorldPos withWorld(Identifier worldId) {
		return new WorldPos(worldId, pos);
	}
}
