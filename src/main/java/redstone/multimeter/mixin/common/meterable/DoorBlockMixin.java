package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.Half;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
			target = "Lnet/minecraft/block/DoorBlock;POWERED:Lnet/minecraft/state/property/BooleanProperty;"
		)
	)
	private void logPowered(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci, boolean powered) {
		rsmm$logPowered(world, pos, powered);
		rsmm$logPowered(world, rsmm$getOtherHalf(pos, state), powered);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(World world, BlockPos pos, BlockState state) {
		return world.hasNeighborSignal(pos) || world.hasNeighborSignal(rsmm$getOtherHalf(pos, state));
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(DoorBlock.OPEN);
	}

	private BlockPos rsmm$getOtherHalf(BlockPos pos, BlockState state) {
		Half half = state.get(DoorBlock.HALF);
		Direction dir = (half == Half.LOWER) ? Direction.UP : Direction.DOWN;

		return pos.offset(dir);
	}
}
