package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockButton;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(BlockButton.class)
public abstract class BlockButtonMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActiveRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockButton.POWERED);
	}
	
	@Override
	public int getPowerLevelRSMM(World world, BlockPos pos, IBlockState state) {
		return state.getValue(BlockButton.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
