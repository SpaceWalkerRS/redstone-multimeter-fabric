package redstone.multimeter.interfaces.mixin;

import java.util.Random;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFluid {
	
	public void randomTick(World world, BlockPos pos, FluidState state, Random random);
	
}
