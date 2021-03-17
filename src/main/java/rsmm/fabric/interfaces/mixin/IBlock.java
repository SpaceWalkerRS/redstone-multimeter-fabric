package rsmm.fabric.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlock {
	
	public boolean isMeterable();
	
	public boolean isPowered(World world, BlockPos pos, BlockState state);
	
}
