package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.MeterableBlock;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin implements MeterableBlock {

	@Shadow private boolean isPoweredByConnectedRails(World world, BlockPos pos, BlockState state, boolean forward, int depth) { return false; }

	@Override
	public boolean rsmm$isPowered(World world, BlockPos pos, BlockState state) {
		return world.hasNeighborSignal(pos) || isPoweredByConnectedRails(world, pos, state, true, 0) || isPoweredByConnectedRails(world, pos, state, false, 0);
	}

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(PoweredRailBlock.POWERED);
	}
}
