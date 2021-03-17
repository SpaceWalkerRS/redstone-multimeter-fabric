package rsmm.fabric.common;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldPos extends BlockPos {
	
	private final Identifier worldId;
	
	public WorldPos(Identifier worldId, BlockPos pos) {
		super(pos.getX(), pos.getY(), pos.getZ());
		
		this.worldId = worldId;
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
	
	public Identifier getWorldId() {
		return worldId;
	}
	
	public boolean isOf(World world) {
		return world.getRegistryKey().getValue().equals(worldId);
	}
}
