package redstone.multimeter.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.class_4023;
import net.minecraft.class_4024;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IFluid;

@Mixin(class_4023.class)
public abstract class FluidMixin implements IFluid {
	
	@Shadow protected abstract void method_17788(World world, BlockPos pos, class_4024 state, Random random);
	
	@Override
	public void randomTick(World world, BlockPos pos, class_4024 state, Random random) {
		method_17788(world, pos, state, random);
	}
}
