package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin implements MeterableBlock, PowerSource {

	@Shadow private int calculateTargetStrength(Level world, BlockPos pos) { return 0; }

	@Inject(
		method = "calculateTargetStrength",
		at = @At(
			value = "RETURN"
		)
	)
	private void logPowered(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		rsmm$logPowered(level, pos, cir.getReturnValue() > MIN_POWER);
	}

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return calculateTargetStrength(level, pos) > MIN_POWER;
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RedStoneWireBlock.POWER) > MIN_POWER;
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		return state.getValue(RedStoneWireBlock.POWER);
	}
}
