package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(PressurePlateBlock.class)
public class PressurePlateBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(PressurePlateBlock.POWERED);
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		return state.getValue(PressurePlateBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
