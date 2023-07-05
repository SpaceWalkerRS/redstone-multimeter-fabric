package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin implements MeterableBlock, PowerSource {

	@Shadow private boolean hasNeighborSignal(World world, BlockPos pos, BlockState state) { return false; }

	@Inject(
		method = "hasNeighborSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		rsmm$logPowered(world, pos, cir.getReturnValue()); // floor redstone torches only
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, BlockPos pos, BlockState state) {
		return hasNeighborSignal(world, pos, state);
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(RedstoneTorchBlock.LIT);
	}

	@Override
	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(RedstoneTorchBlock.LIT) ? MAX_POWER : MIN_POWER;
	}
}
