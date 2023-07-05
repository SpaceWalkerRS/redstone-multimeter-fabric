package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(DaylightDetectorBlock.class)
public class DaylightDetectorBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(DaylightDetectorBlock.POWER) > MIN_POWER;
	}

	@Override
	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(DaylightDetectorBlock.POWER);
	}
}
