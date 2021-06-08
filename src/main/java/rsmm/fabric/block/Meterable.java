package rsmm.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Meterable {
	
	public boolean isActive(World world, BlockPos pos, BlockState state);
	
}
