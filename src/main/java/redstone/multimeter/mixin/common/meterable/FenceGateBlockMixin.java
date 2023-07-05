package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(FenceGateBlock.class)
public class FenceGateBlockMixin implements MeterableBlock {

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 0,
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/block/FenceGateBlock;POWERED:Lnet/minecraft/state/property/BooleanProperty;"
		)
	)
	private void logPowered(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci, boolean powered) {
		rsmm$logPowered(world, pos, powered);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(FenceGateBlock.OPEN);
	}
}
