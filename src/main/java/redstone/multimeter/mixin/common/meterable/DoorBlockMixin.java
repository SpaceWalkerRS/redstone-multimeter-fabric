package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import redstone.multimeter.block.MeterableBlock;

@Mixin(DoorBlock.class)
public class DoorBlockMixin implements MeterableBlock {

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 0,
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/block/DoorBlock;POWERED:Lnet/minecraft/world/level/block/state/properties/BooleanProperty;"
		)
	)
	private void logPowered(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci, boolean powered) {
		rsmm$logPowered(level, pos, powered);
		rsmm$logPowered(level, rsmm$getOtherHalf(pos, state), powered);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return level.hasNeighborSignal(pos) || level.hasNeighborSignal(rsmm$getOtherHalf(pos, state));
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(DoorBlock.OPEN);
	}

	private BlockPos rsmm$getOtherHalf(BlockPos pos, BlockState state) {
		DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
		Direction dir = (half == DoubleBlockHalf.LOWER) ? Direction.UP : Direction.DOWN;

		return pos.relative(dir);
	}
}
