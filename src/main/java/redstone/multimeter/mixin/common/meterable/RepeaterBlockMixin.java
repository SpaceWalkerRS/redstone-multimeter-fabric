package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import redstone.multimeter.block.MeterableBlock;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin implements MeterableBlock {

	@Inject(
		method = "isLocked",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && world instanceof ServerWorld) {
			rsmm$logPowered((ServerWorld)world, pos, state);
		}
	}
}
