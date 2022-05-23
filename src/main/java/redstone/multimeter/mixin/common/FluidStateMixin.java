package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(FluidState.class)
public class FluidStateMixin {
	
	@Inject(
			method = "onRandomTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRandomTick(World world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
		}
	}
}
