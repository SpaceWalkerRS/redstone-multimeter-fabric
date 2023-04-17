package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DiodeBlock.class)
public class DiodeBlockMixin implements MeterableBlock {

	@Shadow private boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) { return false; }

	@Inject(
		method = "shouldTurnOn",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(level, pos, cir.getReturnValue()); // repeaters
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return shouldTurnOn(level, pos, state);
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(DiodeBlock.POWERED);
	}
}
