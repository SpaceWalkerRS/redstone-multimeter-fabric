package redstone.multimeter.mixin.common.meterable;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(BlockRedstoneLight.class)
public class BlockRedstoneLightMixin implements MeterableBlock {
	
	@Shadow @Final private boolean isOn;
	
	@Inject(
			method = "updateTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onScheduledTick(World world, BlockPos pos, IBlockState state, Random random, CallbackInfo ci) {
		logPoweredRSMM(world, pos, state);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return isOn;
	}
}
