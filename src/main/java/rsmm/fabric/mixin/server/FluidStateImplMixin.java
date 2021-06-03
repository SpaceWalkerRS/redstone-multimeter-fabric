package rsmm.fabric.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.FluidStateImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.interfaces.mixin.IServerWorld;
import rsmm.fabric.server.Multimeter;
import rsmm.fabric.server.MultimeterServer;

@Mixin(FluidStateImpl.class)
public abstract class FluidStateImplMixin implements FluidState {
	
	@Override
	public void onRandomTick(World world, BlockPos pos, Random random) {
		MultimeterServer server = ((IServerWorld)world).getMultimeterServer();
		Multimeter multimeter = server.getMultimeter();
		
		multimeter.logRandomTick(world, pos);
		
		FluidState.super.onRandomTick(world, pos, random);
	}
}
