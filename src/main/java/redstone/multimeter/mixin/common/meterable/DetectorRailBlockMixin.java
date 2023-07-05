package redstone.multimeter.mixin.common.meterable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.block.Meterable;
import redstone.multimeter.block.PowerSource;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin implements Meterable, PowerSource {

	@Override
	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state) {
		return state.get(DetectorRailBlock.POWERED);
	}

	@Override
	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(DetectorRailBlock.POWERED) ? MAX_POWER : MIN_POWER;
	}
}
