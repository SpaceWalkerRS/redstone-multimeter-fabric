package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(DaylightDetectorBlock.class)
public class DaylightDetectorBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state) {
		return state.getValue(DaylightDetectorBlock.POWER) > MIN_POWER;
	}

	@Override
	public int rsmm$getPowerLevel(Level level, BlockPos pos, BlockState state) {
		return state.getValue(DaylightDetectorBlock.POWER);
	}
}
