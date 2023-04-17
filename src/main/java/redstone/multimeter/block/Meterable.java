package redstone.multimeter.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.interfaces.mixin.IBlock;

public interface Meterable extends IBlock {

	@Override
	default boolean rsmm$isMeterable() {
		return true;
	}

	public boolean rsmm$isActive(Level level, BlockPos pos, BlockState state);

}
