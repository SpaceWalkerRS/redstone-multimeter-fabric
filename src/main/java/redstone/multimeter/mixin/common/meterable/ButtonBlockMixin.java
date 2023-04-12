package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(ButtonBlock.class)
public class ButtonBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(Level levle, BlockPos pos, BlockState state) {
		return state.getValue(ButtonBlock.POWERED);
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		return state.getValue(ButtonBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
