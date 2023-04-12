package redstone.multimeter.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlock {

	default boolean rsmm$isMeterable() {
		return false;
	}

	default boolean rsmm$isPowerSource() {
		return false;
	}

	default boolean rsmm$logPoweredOnBlockUpdate() {
		return true;
	}

	default boolean rsmm$isPowered(Level level, BlockPos pos, BlockState state) {
		return level.hasNeighborSignal(pos);
	}
}
