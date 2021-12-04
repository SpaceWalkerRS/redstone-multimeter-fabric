package redstone.multimeter.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorBlockMixin implements Meterable, PowerSource {
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(DaylightDetectorBlock.POWER) > MIN_POWER;
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(DaylightDetectorBlock.POWER);
	}
}
