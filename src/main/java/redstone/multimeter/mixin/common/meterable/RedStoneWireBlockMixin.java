package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;

import redstone.multimeter.block.MeterableBlock;
import redstone.multimeter.block.PowerSource;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin implements MeterableBlock, PowerSource {

	@Shadow private RedstoneWireEvaluator evaluator;

	@Override
	public boolean rsmm$logPoweredOnBlockUpdate() {
		return false;
	}

	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return ((DefaultRedstoneWireEvaluatorAccess)evaluator).rsmm$calculateTargetStrength(level, pos) > MIN_POWER;
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
