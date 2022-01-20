package redstone.multimeter.mixin.common.meterable;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneLampBlock.class)
public class RedstoneLampBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "onScheduledTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onScheduledTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
		logPoweredRSMM(world, pos, state);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.LIT);
	}
}
