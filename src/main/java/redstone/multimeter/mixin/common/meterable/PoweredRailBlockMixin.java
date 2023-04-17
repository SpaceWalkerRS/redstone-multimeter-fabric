package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin implements MeterableBlock {

	@Shadow private boolean findPoweredRailSignal(Level level, BlockPos pos, BlockState state, boolean forward, int depth) { return false; }

	@Override
	public boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return level.hasNeighborSignal(pos) || findPoweredRailSignal(level, pos, state, true, 0) || findPoweredRailSignal(level, pos, state, false, 0);
	}

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(PoweredRailBlock.POWERED);
	}
}
