package redstone.multimeter.block;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

	public int rsmm$getPowerLevel(World world, BlockPos pos, BlockState state);

}
