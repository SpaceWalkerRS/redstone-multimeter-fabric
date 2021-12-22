package redstone.multimeter.interfaces.mixin;

import java.util.Random;

import net.minecraft.class_4024;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFluid {
	
	public void randomTick(World world, BlockPos pos, class_4024 state, Random random);
	
}
