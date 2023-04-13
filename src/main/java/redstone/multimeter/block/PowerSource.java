package redstone.multimeter.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import redstone.multimeter.interfaces.mixin.IBlock;

public interface PowerSource extends IBlock {

	public static final int MIN_POWER = 0;
	public static final int MAX_POWER = 15;

	@Override
	default boolean rsmm$isPowerSource() {
		return true;
	}

	default boolean rsmm$logPowerChangeOnStateChange() {
		return true;
	}

	public int rsmm$getPowerLevel(Level world, BlockPos pos, BlockState state);

}
