package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(BlockRailDetector.class)
public abstract class BlockRailDetectorMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockRailDetector.POWERED);
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockRailDetector.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
