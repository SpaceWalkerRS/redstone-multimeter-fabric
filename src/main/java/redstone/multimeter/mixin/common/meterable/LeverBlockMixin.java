package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.LeverBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(LeverBlock.class)
public class LeverBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(LeverBlock.POWERED);
	}

	@Override
	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(LeverBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
