package rsmm.fabric.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rsmm.fabric.server.Meterable;

public interface IBlock {
	
	default boolean isMeterable() {
		return this instanceof Meterable;
	}
	
	public boolean standardIsPowered();
	
	public boolean isPowered(World world, BlockPos pos, BlockState state);
	
}
