package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(TripwireHookBlock.class)
public class TripwireHookBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(TripwireHookBlock.POWERED);
	}

	@Override
	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(TripwireHookBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
