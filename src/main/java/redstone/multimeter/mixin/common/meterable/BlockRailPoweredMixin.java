package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;

@Mixin(BlockRailPowered.class)
public abstract class BlockRailPoweredMixin implements Meterable {
	
	@Shadow protected abstract boolean findPoweredRailSignal(World world, BlockPos pos, IBlockState state, boolean boolean4, int distance);
	
	@Override
	public boolean isPoweredRSMM(World world, BlockPos pos, IBlockState state) {
		return world.isBlockPowered(pos) || findPoweredRailSignal(world, pos, state, true, 0) || findPoweredRailSignal(world, pos, state, false, 0);
	}
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockRailPowered.POWERED);
	}
}
