package rsmm.fabric.common;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

public class WorldPos {
	
	private final Identifier worldId;
	private final BlockPos blockPos;
	
	public WorldPos(Identifier worldId, BlockPos blockPos) {
		this.worldId = worldId;
		this.blockPos = blockPos.toImmutable();
	}
	
	public WorldPos(World world, BlockPos pos) {
		this(world.getRegistryKey().getValue(), pos);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WorldPos) {
			WorldPos pos = (WorldPos)obj;
			return pos.worldId.equals(worldId) && pos.blockPos.equals(blockPos);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return blockPos.hashCode() + 31 * worldId.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%d, %d, %d]", worldId.toString(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public Identifier getWorldId() {
		return worldId;
	}
	
	public boolean isOf(World world) {
		return world.getRegistryKey().getValue().equals(worldId);
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public WorldPos offset(Direction dir) {
		return offset(dir, 1);
	}
	
	public WorldPos offset(Direction dir, int distance) {
		return new WorldPos(worldId, blockPos.offset(dir, distance));
	}
	
	public WorldPos offset(Axis axis) {
		return offset(axis, 1);
	}
	
	public WorldPos offset(Axis axis, int distance) {
		return new WorldPos(worldId, blockPos.offset(axis, distance));
	}
	
	public WorldPos offset(int dx, int dy, int dz) {
		return new WorldPos(worldId, blockPos.add(dx, dy, dz));
	}
	
	/**
	 * Return a WorldPos with the same coordinates in a different dimension.
	 */
	public WorldPos withWorld(Identifier worldId) {
		return new WorldPos(worldId, blockPos);
	}
	
	public WorldPos withCoord(Axis axis, int coord) {
		int x = (axis == Axis.X) ? coord : blockPos.getX();
		int y = (axis == Axis.Y) ? coord : blockPos.getY();
		int z = (axis == Axis.Z) ? coord : blockPos.getZ();
		
		return new WorldPos(worldId, new BlockPos(x, y, z));
	}
}
