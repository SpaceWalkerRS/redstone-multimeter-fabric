package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin implements Meterable, PowerSource {

	@Override
	public int rsmm$getPowerLevel(Level world, BlockPos pos, BlockState state) {
		return state.getValue(SculkSensorBlock.POWER);
	}

	@Override
	public boolean rsmm$isActive(Level world, BlockPos pos, BlockState state) {
		return state.getValue(SculkSensorBlock.POWER) > MIN_POWER;
	}
}
