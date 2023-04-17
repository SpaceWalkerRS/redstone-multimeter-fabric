package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RedstoneWallTorchBlock.class)
public abstract class RedstoneWallTorchBlockMixin implements MeterableBlock {

	@Inject(
		method = "hasNeighborSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(Level world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, pos, cir.getReturnValue());
	}
}
