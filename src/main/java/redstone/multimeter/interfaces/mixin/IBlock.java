package redstone.multimeter.interfaces.mixin;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

	default boolean rsmm$isPowered(World world, BlockPos pos, BlockState state) {
		return world.hasNeighborSignal(pos);
	}
}
