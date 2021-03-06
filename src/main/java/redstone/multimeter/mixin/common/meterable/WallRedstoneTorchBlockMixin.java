package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(WallRedstoneTorchBlock.class)
public abstract class WallRedstoneTorchBlockMixin implements MeterableBlock {
	
	@Inject(
			method = "shouldUnpower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onPowerCheck(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		logPoweredRSMM(world, pos, cir.getReturnValue());
	}
}
