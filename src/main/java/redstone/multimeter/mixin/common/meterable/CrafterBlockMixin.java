package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

import redstone.multimeter.block.MeterableBlock;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin implements MeterableBlock {

	@Inject(
		method = "neighborChanged",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			ordinal = 0,
			shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/block/CrafterBlock;TRIGGERED:Lnet/minecraft/world/level/block/state/properties/BooleanProperty;"
		)
	)
	private void logPowered(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston, CallbackInfo ci, boolean powered) {
		rsmm$logPowered(level, pos, powered);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(CrafterBlock.TRIGGERED);
	}
}
