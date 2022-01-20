package redstone.multimeter.mixin.common;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.FluidStateImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IFluid;
import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(FluidStateImpl.class)
public abstract class FluidStateImplMixin implements FluidState {
	
	@Override
	public void onRandomTick(World world, BlockPos pos, Random random) {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
		}
		((IFluid)getFluid()).randomTickRSMM(world, pos, this, random);
	}
}
