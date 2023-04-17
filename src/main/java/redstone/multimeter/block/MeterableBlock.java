package redstone.multimeter.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.interfaces.mixin.IServerLevel;

public interface MeterableBlock extends Meterable {

	default void rsmm$logPowered(Level level, BlockPos pos, boolean powered) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logPowered(level, pos, powered);
		}
	}

	default void rsmm$logPowered(Level level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getMultimeter().logPowered(level, pos, state);
		}
	}
}
