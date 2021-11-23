package redstone.multimeter.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IFluid;

@Mixin(Fluid.class)
public abstract class FluidMixin implements IFluid {
	
	@Shadow protected abstract void onRandomTick(World world, BlockPos pos, FluidState state, Random random);
	
	@Override
	public void randomTick(World world, BlockPos pos, FluidState state, Random random) {
		onRandomTick(world, pos, state, random);
	}
}
