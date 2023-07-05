package redstone.multimeter.mixin.common.meterable;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin implements MeterableBlock {

	@Shadow @Final private boolean lit;

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private void logPowered(World world, BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
		rsmm$logPowered(world, pos, state);
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return lit;
	}
}
