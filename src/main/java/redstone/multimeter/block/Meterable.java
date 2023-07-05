package redstone.multimeter.block;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstone.multimeter.interfaces.mixin.IBlock;

public interface Meterable extends IBlock {

	@Override
	default boolean rsmm$isMeterable() {
		return true;
	}

	public boolean rsmm$isActive(World world, BlockPos pos, BlockState state);

}
