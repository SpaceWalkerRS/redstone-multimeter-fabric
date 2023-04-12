package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin implements MeterableBlock, PowerSource {

	@Shadow private boolean hasNeighborSignal(Level level, BlockPos pos, BlockState state) { return false; }

	@Inject(
		method = "hasNeighborSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(level, pos, cir.getReturnValue()); // floor redstone torches only
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return hasNeighborSignal(level, pos, state);
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RedstoneTorchBlock.LIT);
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RedstoneTorchBlock.LIT) ? MAX_POWER : MIN_POWER;
	}
}
