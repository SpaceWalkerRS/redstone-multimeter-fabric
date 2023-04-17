package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(Level world, BlockPos pos, BlockState state) {
		return state.getValue(ObserverBlock.POWERED);
	}

	@Override
	public int rsmm$getPowerLevel(Level world, BlockPos pos, BlockState state) {
		return state.getValue(ObserverBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
