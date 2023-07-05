package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.RedstoneWallTorchBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneWallTorchBlock.class)
public abstract class RedstoneWallTorchBlockMixin implements MeterableBlock {

	@Inject(
		method = "hasNeighborSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, pos, cir.getReturnValue());
	}
}
